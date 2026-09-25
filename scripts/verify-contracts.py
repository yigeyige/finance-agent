#!/usr/bin/env python3
"""Verify the repository's versioned contract sources and fixtures.

This intentionally uses only the Python standard library. It covers the subset of
JSON Schema keywords frozen by DEV-BOOT-006 and keeps the command reproducible on
developer machines and CI without adding a runtime dependency.
"""

from __future__ import annotations

import argparse
import json
import re
import sys
import uuid
from datetime import datetime
from pathlib import Path
from typing import Any


class ContractValidationError(ValueError):
    """Raised when a JSON value does not satisfy a contract schema."""


def _type_matches(value: Any, expected: str) -> bool:
    if expected == "object":
        return isinstance(value, dict)
    if expected == "array":
        return isinstance(value, list)
    if expected == "string":
        return isinstance(value, str)
    if expected == "integer":
        return isinstance(value, int) and not isinstance(value, bool)
    if expected == "number":
        return isinstance(value, (int, float)) and not isinstance(value, bool)
    if expected == "boolean":
        return isinstance(value, bool)
    if expected == "null":
        return value is None
    return True


def _validate_format(value: Any, format_name: str, path: str) -> None:
    if not isinstance(value, str):
        raise ContractValidationError(f"{path}: expected string for format {format_name}")
    if format_name == "uuid":
        try:
            uuid.UUID(value)
        except ValueError as exc:
            raise ContractValidationError(f"{path}: invalid uuid") from exc
    elif format_name == "date-time":
        if not re.fullmatch(r"\d{4}-\d{2}-\d{2}T\d{2}:\d{2}:\d{2}(?:\.\d+)?(?:Z|[+-]\d{2}:\d{2})", value):
            raise ContractValidationError(f"{path}: invalid RFC3339 date-time")
        try:
            datetime.fromisoformat(value.replace("Z", "+00:00"))
        except ValueError as exc:
            raise ContractValidationError(f"{path}: invalid date-time value") from exc


def validate(value: Any, schema: dict[str, Any], path: str = "$", root_schema: dict[str, Any] | None = None) -> None:
    expected_type = schema.get("type")
    if expected_type is not None:
        expected_types = expected_type if isinstance(expected_type, list) else [expected_type]
        if not any(_type_matches(value, item) for item in expected_types):
            raise ContractValidationError(f"{path}: expected {expected_types}, got {type(value).__name__}")

    if "enum" in schema and value not in schema["enum"]:
        raise ContractValidationError(f"{path}: value is not one of {schema['enum']}")

    if "pattern" in schema:
        if not isinstance(value, str) or re.fullmatch(schema["pattern"], value) is None:
            raise ContractValidationError(f"{path}: does not match pattern {schema['pattern']}")

    if "format" in schema:
        _validate_format(value, schema["format"], path)

    if isinstance(value, str):
        if len(value) < schema.get("minLength", 0):
            raise ContractValidationError(f"{path}: shorter than minLength")
        if len(value) > schema.get("maxLength", float("inf")):
            raise ContractValidationError(f"{path}: longer than maxLength")

    if isinstance(value, (int, float)) and not isinstance(value, bool):
        if value < schema.get("minimum", float("-inf")):
            raise ContractValidationError(f"{path}: below minimum")
        if value > schema.get("maximum", float("inf")):
            raise ContractValidationError(f"{path}: above maximum")

    if isinstance(value, list):
        if len(value) < schema.get("minItems", 0):
            raise ContractValidationError(f"{path}: fewer than minItems")
        if len(value) > schema.get("maxItems", float("inf")):
            raise ContractValidationError(f"{path}: more than maxItems")
        if schema.get("uniqueItems"):
            encoded = [json.dumps(item, sort_keys=True, separators=(",", ":")) for item in value]
            if len(encoded) != len(set(encoded)):
                raise ContractValidationError(f"{path}: items are not unique")
        item_schema = schema.get("items")
        if item_schema:
            for index, item in enumerate(value):
                validate(item, item_schema, f"{path}[{index}]")

    if isinstance(value, dict):
        required = schema.get("required", [])
        missing = [key for key in required if key not in value]
        if missing:
            raise ContractValidationError(f"{path}: missing required properties {missing}")
        properties = schema.get("properties", {})
        if schema.get("additionalProperties") is False:
            unknown = sorted(set(value) - set(properties))
            if unknown:
                raise ContractValidationError(f"{path}: additional properties {unknown}")
        for key, child_schema in properties.items():
            if key in value:
                validate(value[key], child_schema, f"{path}.{key}")


def assert_schema_metadata(schema_path: Path, schema: dict[str, Any]) -> None:
    if schema.get("$schema") != "https://json-schema.org/draft/2020-12/schema":
        raise ContractValidationError(f"{schema_path}: must use Draft 2020-12")
    schema_id = schema.get("$id", "")
    if not isinstance(schema_id, str) or not re.fullmatch(r"schema://[^/]+(?:/[^/]+)*/\d+\.\d+\.\d+", schema_id):
        raise ContractValidationError(f"{schema_path}: $id must be a semantic-versioned schema:// URI")
    if not re.fullmatch(r"\d+\.\d+\.\d+", str(schema.get("x-contract-version", ""))):
        raise ContractValidationError(f"{schema_path}: x-contract-version must be semantic version")

    def walk(node: Any, path: str) -> None:
        if isinstance(node, dict):
            if node.get("type") == "object" and "additionalProperties" not in node:
                raise ContractValidationError(f"{schema_path}: {path} omits additionalProperties")
            for key, child in node.items():
                walk(child, f"{path}.{key}")
        elif isinstance(node, list):
            for index, child in enumerate(node):
                walk(child, f"{path}[{index}]")

    walk(schema, "$" )


def verify_openapi(openapi_path: Path, schema_paths: dict[str, Path]) -> None:
    document = json.loads(openapi_path.read_text(encoding="utf-8"))
    if document.get("openapi") != "3.1.0":
        raise ContractValidationError(f"{openapi_path}: openapi must be 3.1.0")
    if not document.get("info", {}).get("version"):
        raise ContractValidationError(f"{openapi_path}: info.version is required")
    if document.get("x-contract-version") != document["info"]["version"]:
        raise ContractValidationError(f"{openapi_path}: x-contract-version must match info.version")
    paths = document.get("paths", {})
    required_paths = {"/api/v1/tasks", "/api/v1/tasks/{taskId}/step-approvals", "/api/v1/tasks/{taskId}/publish"}
    if not required_paths.issubset(paths):
        raise ContractValidationError(f"{openapi_path}: missing required paths")

    def walk_refs(node: Any) -> None:
        if isinstance(node, dict):
            ref = node.get("$ref")
            if isinstance(ref, str) and ref.startswith("../json-schema/"):
                filename = ref.removeprefix("../json-schema/").split("#", 1)[0]
                if filename not in schema_paths:
                    raise ContractValidationError(f"{openapi_path}: missing referenced schema {filename}")
            for child in node.values():
                walk_refs(child)
        elif isinstance(node, list):
            for child in node:
                walk_refs(child)

    walk_refs(document)


def verify_examples(example_dir: Path, schemas: dict[str, dict[str, Any]]) -> None:
    for example_path in sorted(example_dir.glob("*.json")):
        parts = example_path.name.split(".")
        if len(parts) < 3 or parts[-1] != "json":
            continue
        contract_name = parts[0]
        markers = parts[:-1]
        expectation = (
            "invalid"
            if any(marker == "invalid" or marker.startswith("invalid-") for marker in markers)
            else "valid"
            if any(marker == "valid" or marker.startswith("valid-") for marker in markers)
            else None
        )
        if expectation is None:
            raise ContractValidationError(f"{example_path}: filename must include valid or invalid")
        if contract_name not in schemas:
            raise ContractValidationError(f"{example_path}: no schema mapping for {contract_name}")
        value = json.loads(example_path.read_text(encoding="utf-8"))
        try:
            validate(value, schemas[contract_name])
        except ContractValidationError as exc:
            if expectation == "valid":
                raise ContractValidationError(f"{example_path}: valid example failed: {exc}") from exc
        else:
            if expectation == "invalid":
                raise ContractValidationError(f"{example_path}: invalid example unexpectedly passed")


def main() -> int:
    parser = argparse.ArgumentParser(description="Verify finance-agent contract sources")
    parser.add_argument("--root", type=Path, default=Path(__file__).resolve().parents[1])
    args = parser.parse_args()
    root = args.root.resolve()
    contract_root = root / "contracts"
    schema_dir = contract_root / "json-schema"
    example_dir = contract_root / "examples"
    schema_paths = {path.name: path for path in sorted(schema_dir.glob("*.schema.json"))}
    schemas = {}

    try:
        if not schema_paths:
            raise ContractValidationError("no JSON Schema files found")
        for filename, schema_path in schema_paths.items():
            schema = json.loads(schema_path.read_text(encoding="utf-8"))
            assert_schema_metadata(schema_path, schema)
            schemas[filename.removesuffix("-1.0.0.schema.json")] = schema
        verify_openapi(contract_root / "openapi" / "finance-agent-v1.json", schema_paths)
        verify_examples(example_dir, schemas)
    except (OSError, json.JSONDecodeError, ContractValidationError) as exc:
        print(f"契约校验失败：{exc}", file=sys.stderr)
        return 1

    print(f"契约校验通过：{len(schema_paths)} 个 schema，{len(list(example_dir.glob('*.json')))} 个样例")
    return 0


if __name__ == "__main__":
    raise SystemExit(main())
