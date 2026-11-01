package cn.com.crc.ems.finance.agent.harness.application;

import java.util.ArrayList;
import java.util.HashSet;
import java.util.LinkedHashMap;
import java.util.LinkedHashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import com.fasterxml.jackson.databind.JsonNode;

/**
 *
 * @Description: 一次用户动作对应的有序只读查询计划；步骤之间通过 dependsOn 和参数绑定表达依赖。
 *
 * @Version: 0.1.0
 * @Author: yigeyige99
 * @Date: 2026-11-01 20:00:00
 */
public record EmsDatabaseQueryPlan(List<Step> steps) {

    public static final int MAX_STEPS = 5;
    private static final Pattern ID = Pattern.compile("[a-z][a-z0-9_]{0,31}");
    private static final Pattern COLUMN = Pattern.compile("[A-Za-z_][A-Za-z0-9_]{0,63}");
    private static final Pattern PLACEHOLDER = Pattern.compile(":([A-Za-z][A-Za-z0-9_]{0,63})");
    private static final Pattern DML = Pattern.compile(
            "(?i)\\b(insert|update|delete|drop|alter|truncate|create|grant|revoke|merge)\\b");

    public EmsDatabaseQueryPlan {
        steps = steps == null ? List.of() : List.copyOf(steps);
        if (steps.size() > MAX_STEPS) {
            throw new IllegalArgumentException("查询计划不能超过 " + MAX_STEPS + " 步");
        }
        validateDependencies(steps);
    }

    /** 从模型严格 JSON 解析查询计划；空 steps 表示本轮不查询。 */
    public static EmsDatabaseQueryPlan fromJson(JsonNode value) {
        if (value == null || !value.isObject()) {
            throw new IllegalArgumentException("查询计划必须是对象");
        }
        JsonNode stepsNode = value.get("steps");
        if (stepsNode == null || !stepsNode.isArray()) {
            throw new IllegalArgumentException("查询计划缺少 steps 数组");
        }
        List<Step> steps = new ArrayList<>();
        for (JsonNode item : stepsNode) {
            steps.add(Step.fromJson(item));
        }
        return new EmsDatabaseQueryPlan(steps);
    }

    private static void validateDependencies(List<Step> steps) {
        Set<String> ids = new HashSet<>();
        for (Step step : steps) {
            if (!ids.add(step.id())) {
                throw new IllegalArgumentException("查询步骤 id 重复: " + step.id());
            }
        }
        for (Step step : steps) {
            for (String dependency : step.dependsOn()) {
                if (!ids.contains(dependency)) {
                    throw new IllegalArgumentException("查询步骤依赖不存在: " + dependency);
                }
                if (dependency.equals(step.id())) {
                    throw new IllegalArgumentException("查询步骤不能依赖自身: " + step.id());
                }
            }
        }
        detectCycle(steps);
    }

    private static void detectCycle(List<Step> steps) {
        Map<String, Step> byId = new LinkedHashMap<>();
        steps.forEach(step -> byId.put(step.id(), step));
        Set<String> visiting = new HashSet<>();
        Set<String> visited = new HashSet<>();
        for (Step step : steps) {
            visit(step.id(), byId, visiting, visited);
        }
    }

    private static void visit(String id, Map<String, Step> byId, Set<String> visiting, Set<String> visited) {
        if (visited.contains(id)) {
            return;
        }
        if (!visiting.add(id)) {
            throw new IllegalArgumentException("查询步骤存在循环依赖: " + id);
        }
        for (String dependency : byId.get(id).dependsOn()) {
            visit(dependency, byId, visiting, visited);
        }
        visiting.remove(id);
        visited.add(id);
    }

    /** 单个查询步骤。 */
    public record Step(String id, String purpose, String sql, List<String> dependsOn,
            Map<String, Binding> bindings) {

        public Step {
            if (id == null || !ID.matcher(id).matches()) {
                throw new IllegalArgumentException("查询步骤 id 不合法");
            }
            if (purpose == null || purpose.isBlank() || purpose.length() > 300) {
                throw new IllegalArgumentException("查询步骤 purpose 不合法");
            }
            if (sql == null || sql.isBlank() || sql.length() > 4_000) {
                throw new IllegalArgumentException("查询步骤 sql 不合法");
            }
            String normalized = sql.toLowerCase();
            if (sql.contains("${") || sql.contains(";") || DML.matcher(normalized).find()) {
                throw new IllegalArgumentException("查询步骤 sql 包含不允许的内容");
            }
            dependsOn = dependsOn == null ? List.of() : List.copyOf(new LinkedHashSet<>(dependsOn));
            bindings = bindings == null ? Map.of() : Map.copyOf(bindings);
            validateBindings(sql, dependsOn, bindings);
        }

        static Step fromJson(JsonNode value) {
            if (value == null || !value.isObject()) {
                throw new IllegalArgumentException("查询步骤必须是对象");
            }
            List<String> dependsOn = new ArrayList<>();
            JsonNode dependencies = value.get("dependsOn");
            if (dependencies != null) {
                if (!dependencies.isArray()) {
                    throw new IllegalArgumentException("dependsOn 必须是数组");
                }
                dependencies.forEach(item -> {
                    if (!item.isTextual() || !ID.matcher(item.textValue()).matches()) {
                        throw new IllegalArgumentException("dependsOn 包含非法 id");
                    }
                    dependsOn.add(item.textValue());
                });
            }
            Map<String, Binding> bindings = new LinkedHashMap<>();
            JsonNode bindingNode = value.get("bindings");
            if (bindingNode != null) {
                if (!bindingNode.isObject()) {
                    throw new IllegalArgumentException("bindings 必须是对象");
                }
                bindingNode.fieldNames().forEachRemaining(name -> {
                    JsonNode item = bindingNode.get(name);
                    if (item == null || !item.isObject()) {
                        throw new IllegalArgumentException("binding 必须是对象: " + name);
                    }
                    String literal = item.has("value") ? item.path("value").asText() : null;
                    String stepId = literal == null ? item.path("stepId").asText() : null;
                    String column = literal == null ? item.path("column").asText() : null;
                    bindings.put(name, new Binding(stepId, column, literal));
                });
            }
            return new Step(value.path("id").asText(), value.path("purpose").asText(),
                    value.path("sql").asText(), dependsOn, bindings);
        }

        private static void validateBindings(String sql, List<String> dependsOn, Map<String, Binding> bindings) {
            Set<String> placeholders = new LinkedHashSet<>();
            Matcher matcher = PLACEHOLDER.matcher(sql);
            while (matcher.find()) {
                placeholders.add(matcher.group(1));
            }
            if (!placeholders.equals(bindings.keySet())) {
                throw new IllegalArgumentException("SQL 占位符与 bindings 不一致");
            }
            for (Binding binding : bindings.values()) {
                if (binding == null) {
                    throw new IllegalArgumentException("binding 不合法");
                }
                if (binding.value() != null) {
                    if (binding.value().isBlank() || binding.value().length() > 500) {
                        throw new IllegalArgumentException("binding value 不合法");
                    }
                    continue;
                }
                if (binding.stepId() == null || !ID.matcher(binding.stepId()).matches()
                        || binding.column() == null || !COLUMN.matcher(binding.column()).matches()) {
                    throw new IllegalArgumentException("binding 不合法");
                }
                if (!dependsOn.contains(binding.stepId())) {
                    throw new IllegalArgumentException("binding 引用的步骤必须出现在 dependsOn 中");
                }
            }
        }
    }

    /** 从前序步骤首行指定列取值，或使用模型提供的字面量，作为 JDBC 参数。 */
    public record Binding(String stepId, String column, String value) {
    }
}
