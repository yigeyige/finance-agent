-- 人员配置离职检查：排除 sys_emp_job，并取消证据查询的 20 行人为上限。
UPDATE task.business_scene_skill
SET investigation_prompt = replace(
        replace(
            replace(
                replace(investigation_prompt,
                    $a$- 账号与任职：basedata.sys_register_user、sys_emp、sys_emp_job（任职、离职日期、HR 状态）。$a$,
                    $b$- 账号与任职：basedata.sys_register_user、sys_emp（账号与任职基础信息）。
- 不要查询 basedata.sys_emp_job（已停用/不用于本场景）。$b$),
                $c$2. 先定位人员，再查各配置表；每轮最多 5 步，单步最多返回 20 行。$c$,
                $d$2. 先定位人员，再查各配置表；每轮最多 5 步。单步查询返回全部匹配记录，不在 SQL 中人为加 LIMIT；模型分析时再按上下文预算取舍。$d$),
            $e$rows 每表最多 20 行；超过 10 张时保留最关键的 10 张$e$,
            $f$rows 返回该表全部匹配记录；超过 10 张时保留最关键的 10 张$f$),
        $g$draft.evidence 每张有数据的表一条，含 schema、table、purpose、columns、rows，$g$,
        $h$draft.evidence 每张有数据的表一条，含 schema、table、purpose、columns、rows，$h$),
    table_hints = replace(table_hints, 'basedata.sys_emp_job ', ''),
    updated_at = now()
WHERE skill_id = '6ec7774b-b226-4a7c-be5f-49349c3a14d8';
