-- create database finance_agent (connection scope)
CREATE DATABASE "finance_agent";

-- grant pg_checkpoint access to finance_agent (connection scope)
GRANT ALL PRIVILEGES ON DATABASE "finance_agent" TO "pg_checkpoint";

-- grant current schemas to pg_checkpoint in finance_agent (database: finance_agent)
GRANT ALL PRIVILEGES ON SCHEMA "public" TO "pg_checkpoint";

-- grant current tables to pg_checkpoint in finance_agent (database: finance_agent)
GRANT ALL PRIVILEGES ON ALL TABLES IN SCHEMA "public" TO "pg_checkpoint";

-- grant current sequences to pg_checkpoint in finance_agent (database: finance_agent)
GRANT ALL PRIVILEGES ON ALL SEQUENCES IN SCHEMA "public" TO "pg_checkpoint";

-- grant current functions to pg_checkpoint in finance_agent (database: finance_agent)
GRANT ALL PRIVILEGES ON ALL FUNCTIONS IN SCHEMA "public" TO "pg_checkpoint";

-- grant future schemas from current role to pg_checkpoint in finance_agent (database: finance_agent)
ALTER DEFAULT PRIVILEGES GRANT ALL PRIVILEGES ON SCHEMAS TO "pg_checkpoint";

-- grant future tables from current role to pg_checkpoint in finance_agent (database: finance_agent)
ALTER DEFAULT PRIVILEGES GRANT ALL PRIVILEGES ON TABLES TO "pg_checkpoint";

-- grant future sequences from current role to pg_checkpoint in finance_agent (database: finance_agent)
ALTER DEFAULT PRIVILEGES GRANT ALL PRIVILEGES ON SEQUENCES TO "pg_checkpoint";

-- grant future functions from current role to pg_checkpoint in finance_agent (database: finance_agent)
ALTER DEFAULT PRIVILEGES GRANT ALL PRIVILEGES ON FUNCTIONS TO "pg_checkpoint";

-- grant pg_create_subscription access to finance_agent (connection scope)
GRANT ALL PRIVILEGES ON DATABASE "finance_agent" TO "pg_create_subscription";

-- grant current schemas to pg_create_subscription in finance_agent (database: finance_agent)
GRANT ALL PRIVILEGES ON SCHEMA "public" TO "pg_create_subscription";

-- grant current tables to pg_create_subscription in finance_agent (database: finance_agent)
GRANT ALL PRIVILEGES ON ALL TABLES IN SCHEMA "public" TO "pg_create_subscription";

-- grant current sequences to pg_create_subscription in finance_agent (database: finance_agent)
GRANT ALL PRIVILEGES ON ALL SEQUENCES IN SCHEMA "public" TO "pg_create_subscription";

-- grant current functions to pg_create_subscription in finance_agent (database: finance_agent)
GRANT ALL PRIVILEGES ON ALL FUNCTIONS IN SCHEMA "public" TO "pg_create_subscription";

-- grant future schemas from current role to pg_create_subscription in finance_agent (database: finance_agent)
ALTER DEFAULT PRIVILEGES GRANT ALL PRIVILEGES ON SCHEMAS TO "pg_create_subscription";

-- grant future tables from current role to pg_create_subscription in finance_agent (database: finance_agent)
ALTER DEFAULT PRIVILEGES GRANT ALL PRIVILEGES ON TABLES TO "pg_create_subscription";

-- grant future sequences from current role to pg_create_subscription in finance_agent (database: finance_agent)
ALTER DEFAULT PRIVILEGES GRANT ALL PRIVILEGES ON SEQUENCES TO "pg_create_subscription";

-- grant future functions from current role to pg_create_subscription in finance_agent (database: finance_agent)
ALTER DEFAULT PRIVILEGES GRANT ALL PRIVILEGES ON FUNCTIONS TO "pg_create_subscription";

-- grant pg_database_owner access to finance_agent (connection scope)
GRANT ALL PRIVILEGES ON DATABASE "finance_agent" TO "pg_database_owner";

-- grant current schemas to pg_database_owner in finance_agent (database: finance_agent)
GRANT ALL PRIVILEGES ON SCHEMA "public" TO "pg_database_owner";

-- grant current tables to pg_database_owner in finance_agent (database: finance_agent)
GRANT ALL PRIVILEGES ON ALL TABLES IN SCHEMA "public" TO "pg_database_owner";

-- grant current sequences to pg_database_owner in finance_agent (database: finance_agent)
GRANT ALL PRIVILEGES ON ALL SEQUENCES IN SCHEMA "public" TO "pg_database_owner";

-- grant current functions to pg_database_owner in finance_agent (database: finance_agent)
GRANT ALL PRIVILEGES ON ALL FUNCTIONS IN SCHEMA "public" TO "pg_database_owner";

-- grant future schemas from current role to pg_database_owner in finance_agent (database: finance_agent)
ALTER DEFAULT PRIVILEGES GRANT ALL PRIVILEGES ON SCHEMAS TO "pg_database_owner";

-- grant future tables from current role to pg_database_owner in finance_agent (database: finance_agent)
ALTER DEFAULT PRIVILEGES GRANT ALL PRIVILEGES ON TABLES TO "pg_database_owner";

-- grant future sequences from current role to pg_database_owner in finance_agent (database: finance_agent)
ALTER DEFAULT PRIVILEGES GRANT ALL PRIVILEGES ON SEQUENCES TO "pg_database_owner";

-- grant future functions from current role to pg_database_owner in finance_agent (database: finance_agent)
ALTER DEFAULT PRIVILEGES GRANT ALL PRIVILEGES ON FUNCTIONS TO "pg_database_owner";

-- grant pg_execute_server_program access to finance_agent (connection scope)
GRANT ALL PRIVILEGES ON DATABASE "finance_agent" TO "pg_execute_server_program";

-- grant current schemas to pg_execute_server_program in finance_agent (database: finance_agent)
GRANT ALL PRIVILEGES ON SCHEMA "public" TO "pg_execute_server_program";

-- grant current tables to pg_execute_server_program in finance_agent (database: finance_agent)
GRANT ALL PRIVILEGES ON ALL TABLES IN SCHEMA "public" TO "pg_execute_server_program";

-- grant current sequences to pg_execute_server_program in finance_agent (database: finance_agent)
GRANT ALL PRIVILEGES ON ALL SEQUENCES IN SCHEMA "public" TO "pg_execute_server_program";

-- grant current functions to pg_execute_server_program in finance_agent (database: finance_agent)
GRANT ALL PRIVILEGES ON ALL FUNCTIONS IN SCHEMA "public" TO "pg_execute_server_program";

-- grant future schemas from current role to pg_execute_server_program in finance_agent (database: finance_agent)
ALTER DEFAULT PRIVILEGES GRANT ALL PRIVILEGES ON SCHEMAS TO "pg_execute_server_program";

-- grant future tables from current role to pg_execute_server_program in finance_agent (database: finance_agent)
ALTER DEFAULT PRIVILEGES GRANT ALL PRIVILEGES ON TABLES TO "pg_execute_server_program";

-- grant future sequences from current role to pg_execute_server_program in finance_agent (database: finance_agent)
ALTER DEFAULT PRIVILEGES GRANT ALL PRIVILEGES ON SEQUENCES TO "pg_execute_server_program";

-- grant future functions from current role to pg_execute_server_program in finance_agent (database: finance_agent)
ALTER DEFAULT PRIVILEGES GRANT ALL PRIVILEGES ON FUNCTIONS TO "pg_execute_server_program";

-- grant pg_maintain access to finance_agent (connection scope)
GRANT ALL PRIVILEGES ON DATABASE "finance_agent" TO "pg_maintain";

-- grant current schemas to pg_maintain in finance_agent (database: finance_agent)
GRANT ALL PRIVILEGES ON SCHEMA "public" TO "pg_maintain";

-- grant current tables to pg_maintain in finance_agent (database: finance_agent)
GRANT ALL PRIVILEGES ON ALL TABLES IN SCHEMA "public" TO "pg_maintain";

-- grant current sequences to pg_maintain in finance_agent (database: finance_agent)
GRANT ALL PRIVILEGES ON ALL SEQUENCES IN SCHEMA "public" TO "pg_maintain";

-- grant current functions to pg_maintain in finance_agent (database: finance_agent)
GRANT ALL PRIVILEGES ON ALL FUNCTIONS IN SCHEMA "public" TO "pg_maintain";

-- grant future schemas from current role to pg_maintain in finance_agent (database: finance_agent)
ALTER DEFAULT PRIVILEGES GRANT ALL PRIVILEGES ON SCHEMAS TO "pg_maintain";

-- grant future tables from current role to pg_maintain in finance_agent (database: finance_agent)
ALTER DEFAULT PRIVILEGES GRANT ALL PRIVILEGES ON TABLES TO "pg_maintain";

-- grant future sequences from current role to pg_maintain in finance_agent (database: finance_agent)
ALTER DEFAULT PRIVILEGES GRANT ALL PRIVILEGES ON SEQUENCES TO "pg_maintain";

-- grant future functions from current role to pg_maintain in finance_agent (database: finance_agent)
ALTER DEFAULT PRIVILEGES GRANT ALL PRIVILEGES ON FUNCTIONS TO "pg_maintain";

-- grant pg_monitor access to finance_agent (connection scope)
GRANT ALL PRIVILEGES ON DATABASE "finance_agent" TO "pg_monitor";

-- grant current schemas to pg_monitor in finance_agent (database: finance_agent)
GRANT ALL PRIVILEGES ON SCHEMA "public" TO "pg_monitor";

-- grant current tables to pg_monitor in finance_agent (database: finance_agent)
GRANT ALL PRIVILEGES ON ALL TABLES IN SCHEMA "public" TO "pg_monitor";

-- grant current sequences to pg_monitor in finance_agent (database: finance_agent)
GRANT ALL PRIVILEGES ON ALL SEQUENCES IN SCHEMA "public" TO "pg_monitor";

-- grant current functions to pg_monitor in finance_agent (database: finance_agent)
GRANT ALL PRIVILEGES ON ALL FUNCTIONS IN SCHEMA "public" TO "pg_monitor";

-- grant future schemas from current role to pg_monitor in finance_agent (database: finance_agent)
ALTER DEFAULT PRIVILEGES GRANT ALL PRIVILEGES ON SCHEMAS TO "pg_monitor";

-- grant future tables from current role to pg_monitor in finance_agent (database: finance_agent)
ALTER DEFAULT PRIVILEGES GRANT ALL PRIVILEGES ON TABLES TO "pg_monitor";

-- grant future sequences from current role to pg_monitor in finance_agent (database: finance_agent)
ALTER DEFAULT PRIVILEGES GRANT ALL PRIVILEGES ON SEQUENCES TO "pg_monitor";

-- grant future functions from current role to pg_monitor in finance_agent (database: finance_agent)
ALTER DEFAULT PRIVILEGES GRANT ALL PRIVILEGES ON FUNCTIONS TO "pg_monitor";

-- grant pg_read_all_data access to finance_agent (connection scope)
GRANT ALL PRIVILEGES ON DATABASE "finance_agent" TO "pg_read_all_data";

-- grant current schemas to pg_read_all_data in finance_agent (database: finance_agent)
GRANT ALL PRIVILEGES ON SCHEMA "public" TO "pg_read_all_data";

-- grant current tables to pg_read_all_data in finance_agent (database: finance_agent)
GRANT ALL PRIVILEGES ON ALL TABLES IN SCHEMA "public" TO "pg_read_all_data";

-- grant current sequences to pg_read_all_data in finance_agent (database: finance_agent)
GRANT ALL PRIVILEGES ON ALL SEQUENCES IN SCHEMA "public" TO "pg_read_all_data";

-- grant current functions to pg_read_all_data in finance_agent (database: finance_agent)
GRANT ALL PRIVILEGES ON ALL FUNCTIONS IN SCHEMA "public" TO "pg_read_all_data";

-- grant future schemas from current role to pg_read_all_data in finance_agent (database: finance_agent)
ALTER DEFAULT PRIVILEGES GRANT ALL PRIVILEGES ON SCHEMAS TO "pg_read_all_data";

-- grant future tables from current role to pg_read_all_data in finance_agent (database: finance_agent)
ALTER DEFAULT PRIVILEGES GRANT ALL PRIVILEGES ON TABLES TO "pg_read_all_data";

-- grant future sequences from current role to pg_read_all_data in finance_agent (database: finance_agent)
ALTER DEFAULT PRIVILEGES GRANT ALL PRIVILEGES ON SEQUENCES TO "pg_read_all_data";

-- grant future functions from current role to pg_read_all_data in finance_agent (database: finance_agent)
ALTER DEFAULT PRIVILEGES GRANT ALL PRIVILEGES ON FUNCTIONS TO "pg_read_all_data";

-- grant pg_read_all_stats access to finance_agent (connection scope)
GRANT ALL PRIVILEGES ON DATABASE "finance_agent" TO "pg_read_all_stats";

-- grant current schemas to pg_read_all_stats in finance_agent (database: finance_agent)
GRANT ALL PRIVILEGES ON SCHEMA "public" TO "pg_read_all_stats";

-- grant current tables to pg_read_all_stats in finance_agent (database: finance_agent)
GRANT ALL PRIVILEGES ON ALL TABLES IN SCHEMA "public" TO "pg_read_all_stats";

-- grant current sequences to pg_read_all_stats in finance_agent (database: finance_agent)
GRANT ALL PRIVILEGES ON ALL SEQUENCES IN SCHEMA "public" TO "pg_read_all_stats";

-- grant current functions to pg_read_all_stats in finance_agent (database: finance_agent)
GRANT ALL PRIVILEGES ON ALL FUNCTIONS IN SCHEMA "public" TO "pg_read_all_stats";

-- grant future schemas from current role to pg_read_all_stats in finance_agent (database: finance_agent)
ALTER DEFAULT PRIVILEGES GRANT ALL PRIVILEGES ON SCHEMAS TO "pg_read_all_stats";

-- grant future tables from current role to pg_read_all_stats in finance_agent (database: finance_agent)
ALTER DEFAULT PRIVILEGES GRANT ALL PRIVILEGES ON TABLES TO "pg_read_all_stats";

-- grant future sequences from current role to pg_read_all_stats in finance_agent (database: finance_agent)
ALTER DEFAULT PRIVILEGES GRANT ALL PRIVILEGES ON SEQUENCES TO "pg_read_all_stats";

-- grant future functions from current role to pg_read_all_stats in finance_agent (database: finance_agent)
ALTER DEFAULT PRIVILEGES GRANT ALL PRIVILEGES ON FUNCTIONS TO "pg_read_all_stats";

-- grant pg_read_all_settings access to finance_agent (connection scope)
GRANT ALL PRIVILEGES ON DATABASE "finance_agent" TO "pg_read_all_settings";

-- grant current schemas to pg_read_all_settings in finance_agent (database: finance_agent)
GRANT ALL PRIVILEGES ON SCHEMA "public" TO "pg_read_all_settings";

-- grant current tables to pg_read_all_settings in finance_agent (database: finance_agent)
GRANT ALL PRIVILEGES ON ALL TABLES IN SCHEMA "public" TO "pg_read_all_settings";

-- grant current sequences to pg_read_all_settings in finance_agent (database: finance_agent)
GRANT ALL PRIVILEGES ON ALL SEQUENCES IN SCHEMA "public" TO "pg_read_all_settings";

-- grant current functions to pg_read_all_settings in finance_agent (database: finance_agent)
GRANT ALL PRIVILEGES ON ALL FUNCTIONS IN SCHEMA "public" TO "pg_read_all_settings";

-- grant future schemas from current role to pg_read_all_settings in finance_agent (database: finance_agent)
ALTER DEFAULT PRIVILEGES GRANT ALL PRIVILEGES ON SCHEMAS TO "pg_read_all_settings";

-- grant future tables from current role to pg_read_all_settings in finance_agent (database: finance_agent)
ALTER DEFAULT PRIVILEGES GRANT ALL PRIVILEGES ON TABLES TO "pg_read_all_settings";

-- grant future sequences from current role to pg_read_all_settings in finance_agent (database: finance_agent)
ALTER DEFAULT PRIVILEGES GRANT ALL PRIVILEGES ON SEQUENCES TO "pg_read_all_settings";

-- grant future functions from current role to pg_read_all_settings in finance_agent (database: finance_agent)
ALTER DEFAULT PRIVILEGES GRANT ALL PRIVILEGES ON FUNCTIONS TO "pg_read_all_settings";

-- grant pg_read_server_files access to finance_agent (connection scope)
GRANT ALL PRIVILEGES ON DATABASE "finance_agent" TO "pg_read_server_files";

-- grant current schemas to pg_read_server_files in finance_agent (database: finance_agent)
GRANT ALL PRIVILEGES ON SCHEMA "public" TO "pg_read_server_files";

-- grant current tables to pg_read_server_files in finance_agent (database: finance_agent)
GRANT ALL PRIVILEGES ON ALL TABLES IN SCHEMA "public" TO "pg_read_server_files";

-- grant current sequences to pg_read_server_files in finance_agent (database: finance_agent)
GRANT ALL PRIVILEGES ON ALL SEQUENCES IN SCHEMA "public" TO "pg_read_server_files";

-- grant current functions to pg_read_server_files in finance_agent (database: finance_agent)
GRANT ALL PRIVILEGES ON ALL FUNCTIONS IN SCHEMA "public" TO "pg_read_server_files";

-- grant future schemas from current role to pg_read_server_files in finance_agent (database: finance_agent)
ALTER DEFAULT PRIVILEGES GRANT ALL PRIVILEGES ON SCHEMAS TO "pg_read_server_files";

-- grant future tables from current role to pg_read_server_files in finance_agent (database: finance_agent)
ALTER DEFAULT PRIVILEGES GRANT ALL PRIVILEGES ON TABLES TO "pg_read_server_files";

-- grant future sequences from current role to pg_read_server_files in finance_agent (database: finance_agent)
ALTER DEFAULT PRIVILEGES GRANT ALL PRIVILEGES ON SEQUENCES TO "pg_read_server_files";

-- grant future functions from current role to pg_read_server_files in finance_agent (database: finance_agent)
ALTER DEFAULT PRIVILEGES GRANT ALL PRIVILEGES ON FUNCTIONS TO "pg_read_server_files";

-- grant pg_signal_backend access to finance_agent (connection scope)
GRANT ALL PRIVILEGES ON DATABASE "finance_agent" TO "pg_signal_backend";

-- grant current schemas to pg_signal_backend in finance_agent (database: finance_agent)
GRANT ALL PRIVILEGES ON SCHEMA "public" TO "pg_signal_backend";

-- grant current tables to pg_signal_backend in finance_agent (database: finance_agent)
GRANT ALL PRIVILEGES ON ALL TABLES IN SCHEMA "public" TO "pg_signal_backend";

-- grant current sequences to pg_signal_backend in finance_agent (database: finance_agent)
GRANT ALL PRIVILEGES ON ALL SEQUENCES IN SCHEMA "public" TO "pg_signal_backend";

-- grant current functions to pg_signal_backend in finance_agent (database: finance_agent)
GRANT ALL PRIVILEGES ON ALL FUNCTIONS IN SCHEMA "public" TO "pg_signal_backend";

-- grant future schemas from current role to pg_signal_backend in finance_agent (database: finance_agent)
ALTER DEFAULT PRIVILEGES GRANT ALL PRIVILEGES ON SCHEMAS TO "pg_signal_backend";

-- grant future tables from current role to pg_signal_backend in finance_agent (database: finance_agent)
ALTER DEFAULT PRIVILEGES GRANT ALL PRIVILEGES ON TABLES TO "pg_signal_backend";

-- grant future sequences from current role to pg_signal_backend in finance_agent (database: finance_agent)
ALTER DEFAULT PRIVILEGES GRANT ALL PRIVILEGES ON SEQUENCES TO "pg_signal_backend";

-- grant future functions from current role to pg_signal_backend in finance_agent (database: finance_agent)
ALTER DEFAULT PRIVILEGES GRANT ALL PRIVILEGES ON FUNCTIONS TO "pg_signal_backend";

-- grant pg_stat_scan_tables access to finance_agent (connection scope)
GRANT ALL PRIVILEGES ON DATABASE "finance_agent" TO "pg_stat_scan_tables";

-- grant current schemas to pg_stat_scan_tables in finance_agent (database: finance_agent)
GRANT ALL PRIVILEGES ON SCHEMA "public" TO "pg_stat_scan_tables";

-- grant current tables to pg_stat_scan_tables in finance_agent (database: finance_agent)
GRANT ALL PRIVILEGES ON ALL TABLES IN SCHEMA "public" TO "pg_stat_scan_tables";

-- grant current sequences to pg_stat_scan_tables in finance_agent (database: finance_agent)
GRANT ALL PRIVILEGES ON ALL SEQUENCES IN SCHEMA "public" TO "pg_stat_scan_tables";

-- grant current functions to pg_stat_scan_tables in finance_agent (database: finance_agent)
GRANT ALL PRIVILEGES ON ALL FUNCTIONS IN SCHEMA "public" TO "pg_stat_scan_tables";

-- grant future schemas from current role to pg_stat_scan_tables in finance_agent (database: finance_agent)
ALTER DEFAULT PRIVILEGES GRANT ALL PRIVILEGES ON SCHEMAS TO "pg_stat_scan_tables";

-- grant future tables from current role to pg_stat_scan_tables in finance_agent (database: finance_agent)
ALTER DEFAULT PRIVILEGES GRANT ALL PRIVILEGES ON TABLES TO "pg_stat_scan_tables";

-- grant future sequences from current role to pg_stat_scan_tables in finance_agent (database: finance_agent)
ALTER DEFAULT PRIVILEGES GRANT ALL PRIVILEGES ON SEQUENCES TO "pg_stat_scan_tables";

-- grant future functions from current role to pg_stat_scan_tables in finance_agent (database: finance_agent)
ALTER DEFAULT PRIVILEGES GRANT ALL PRIVILEGES ON FUNCTIONS TO "pg_stat_scan_tables";

-- grant pg_use_reserved_connections access to finance_agent (connection scope)
GRANT ALL PRIVILEGES ON DATABASE "finance_agent" TO "pg_use_reserved_connections";

-- grant current schemas to pg_use_reserved_connections in finance_agent (database: finance_agent)
GRANT ALL PRIVILEGES ON SCHEMA "public" TO "pg_use_reserved_connections";

-- grant current tables to pg_use_reserved_connections in finance_agent (database: finance_agent)
GRANT ALL PRIVILEGES ON ALL TABLES IN SCHEMA "public" TO "pg_use_reserved_connections";

-- grant current sequences to pg_use_reserved_connections in finance_agent (database: finance_agent)
GRANT ALL PRIVILEGES ON ALL SEQUENCES IN SCHEMA "public" TO "pg_use_reserved_connections";

-- grant current functions to pg_use_reserved_connections in finance_agent (database: finance_agent)
GRANT ALL PRIVILEGES ON ALL FUNCTIONS IN SCHEMA "public" TO "pg_use_reserved_connections";

-- grant future schemas from current role to pg_use_reserved_connections in finance_agent (database: finance_agent)
ALTER DEFAULT PRIVILEGES GRANT ALL PRIVILEGES ON SCHEMAS TO "pg_use_reserved_connections";

-- grant future tables from current role to pg_use_reserved_connections in finance_agent (database: finance_agent)
ALTER DEFAULT PRIVILEGES GRANT ALL PRIVILEGES ON TABLES TO "pg_use_reserved_connections";

-- grant future sequences from current role to pg_use_reserved_connections in finance_agent (database: finance_agent)
ALTER DEFAULT PRIVILEGES GRANT ALL PRIVILEGES ON SEQUENCES TO "pg_use_reserved_connections";

-- grant future functions from current role to pg_use_reserved_connections in finance_agent (database: finance_agent)
ALTER DEFAULT PRIVILEGES GRANT ALL PRIVILEGES ON FUNCTIONS TO "pg_use_reserved_connections";

-- grant pg_write_all_data access to finance_agent (connection scope)
GRANT ALL PRIVILEGES ON DATABASE "finance_agent" TO "pg_write_all_data";

-- grant current schemas to pg_write_all_data in finance_agent (database: finance_agent)
GRANT ALL PRIVILEGES ON SCHEMA "public" TO "pg_write_all_data";

-- grant current tables to pg_write_all_data in finance_agent (database: finance_agent)
GRANT ALL PRIVILEGES ON ALL TABLES IN SCHEMA "public" TO "pg_write_all_data";

-- grant current sequences to pg_write_all_data in finance_agent (database: finance_agent)
GRANT ALL PRIVILEGES ON ALL SEQUENCES IN SCHEMA "public" TO "pg_write_all_data";

-- grant current functions to pg_write_all_data in finance_agent (database: finance_agent)
GRANT ALL PRIVILEGES ON ALL FUNCTIONS IN SCHEMA "public" TO "pg_write_all_data";

-- grant future schemas from current role to pg_write_all_data in finance_agent (database: finance_agent)
ALTER DEFAULT PRIVILEGES GRANT ALL PRIVILEGES ON SCHEMAS TO "pg_write_all_data";

-- grant future tables from current role to pg_write_all_data in finance_agent (database: finance_agent)
ALTER DEFAULT PRIVILEGES GRANT ALL PRIVILEGES ON TABLES TO "pg_write_all_data";

-- grant future sequences from current role to pg_write_all_data in finance_agent (database: finance_agent)
ALTER DEFAULT PRIVILEGES GRANT ALL PRIVILEGES ON SEQUENCES TO "pg_write_all_data";

-- grant future functions from current role to pg_write_all_data in finance_agent (database: finance_agent)
ALTER DEFAULT PRIVILEGES GRANT ALL PRIVILEGES ON FUNCTIONS TO "pg_write_all_data";

-- grant pg_write_server_files access to finance_agent (connection scope)
GRANT ALL PRIVILEGES ON DATABASE "finance_agent" TO "pg_write_server_files";

-- grant current schemas to pg_write_server_files in finance_agent (database: finance_agent)
GRANT ALL PRIVILEGES ON SCHEMA "public" TO "pg_write_server_files";

-- grant current tables to pg_write_server_files in finance_agent (database: finance_agent)
GRANT ALL PRIVILEGES ON ALL TABLES IN SCHEMA "public" TO "pg_write_server_files";

-- grant current sequences to pg_write_server_files in finance_agent (database: finance_agent)
GRANT ALL PRIVILEGES ON ALL SEQUENCES IN SCHEMA "public" TO "pg_write_server_files";

-- grant current functions to pg_write_server_files in finance_agent (database: finance_agent)
GRANT ALL PRIVILEGES ON ALL FUNCTIONS IN SCHEMA "public" TO "pg_write_server_files";

-- grant future schemas from current role to pg_write_server_files in finance_agent (database: finance_agent)
ALTER DEFAULT PRIVILEGES GRANT ALL PRIVILEGES ON SCHEMAS TO "pg_write_server_files";

-- grant future tables from current role to pg_write_server_files in finance_agent (database: finance_agent)
ALTER DEFAULT PRIVILEGES GRANT ALL PRIVILEGES ON TABLES TO "pg_write_server_files";

-- grant future sequences from current role to pg_write_server_files in finance_agent (database: finance_agent)
ALTER DEFAULT PRIVILEGES GRANT ALL PRIVILEGES ON SEQUENCES TO "pg_write_server_files";

-- grant future functions from current role to pg_write_server_files in finance_agent (database: finance_agent)
ALTER DEFAULT PRIVILEGES GRANT ALL PRIVILEGES ON FUNCTIONS TO "pg_write_server_files";

-- grant postgres access to finance_agent (connection scope)
GRANT ALL PRIVILEGES ON DATABASE "finance_agent" TO "postgres";

-- grant current schemas to postgres in finance_agent (database: finance_agent)
GRANT ALL PRIVILEGES ON SCHEMA "public" TO "postgres";

-- grant current tables to postgres in finance_agent (database: finance_agent)
GRANT ALL PRIVILEGES ON ALL TABLES IN SCHEMA "public" TO "postgres";

-- grant current sequences to postgres in finance_agent (database: finance_agent)
GRANT ALL PRIVILEGES ON ALL SEQUENCES IN SCHEMA "public" TO "postgres";

-- grant current functions to postgres in finance_agent (database: finance_agent)
GRANT ALL PRIVILEGES ON ALL FUNCTIONS IN SCHEMA "public" TO "postgres";

-- grant future schemas from current role to postgres in finance_agent (database: finance_agent)
ALTER DEFAULT PRIVILEGES GRANT ALL PRIVILEGES ON SCHEMAS TO "postgres";

-- grant future tables from current role to postgres in finance_agent (database: finance_agent)
ALTER DEFAULT PRIVILEGES GRANT ALL PRIVILEGES ON TABLES TO "postgres";

-- grant future sequences from current role to postgres in finance_agent (database: finance_agent)
ALTER DEFAULT PRIVILEGES GRANT ALL PRIVILEGES ON SEQUENCES TO "postgres";

-- grant future functions from current role to postgres in finance_agent (database: finance_agent)
ALTER DEFAULT PRIVILEGES GRANT ALL PRIVILEGES ON FUNCTIONS TO "postgres";

-- grant future schemas from pg_checkpoint to pg_create_subscription in finance_agent (database: finance_agent)
ALTER DEFAULT PRIVILEGES FOR ROLE "pg_checkpoint" GRANT ALL PRIVILEGES ON SCHEMAS TO "pg_create_subscription";

-- grant future tables from pg_checkpoint to pg_create_subscription in finance_agent (database: finance_agent)
ALTER DEFAULT PRIVILEGES FOR ROLE "pg_checkpoint" GRANT ALL PRIVILEGES ON TABLES TO "pg_create_subscription";

-- grant future sequences from pg_checkpoint to pg_create_subscription in finance_agent (database: finance_agent)
ALTER DEFAULT PRIVILEGES FOR ROLE "pg_checkpoint" GRANT ALL PRIVILEGES ON SEQUENCES TO "pg_create_subscription";

-- grant future functions from pg_checkpoint to pg_create_subscription in finance_agent (database: finance_agent)
ALTER DEFAULT PRIVILEGES FOR ROLE "pg_checkpoint" GRANT ALL PRIVILEGES ON FUNCTIONS TO "pg_create_subscription";

-- grant future schemas from pg_checkpoint to pg_database_owner in finance_agent (database: finance_agent)
ALTER DEFAULT PRIVILEGES FOR ROLE "pg_checkpoint" GRANT ALL PRIVILEGES ON SCHEMAS TO "pg_database_owner";

-- grant future tables from pg_checkpoint to pg_database_owner in finance_agent (database: finance_agent)
ALTER DEFAULT PRIVILEGES FOR ROLE "pg_checkpoint" GRANT ALL PRIVILEGES ON TABLES TO "pg_database_owner";

-- grant future sequences from pg_checkpoint to pg_database_owner in finance_agent (database: finance_agent)
ALTER DEFAULT PRIVILEGES FOR ROLE "pg_checkpoint" GRANT ALL PRIVILEGES ON SEQUENCES TO "pg_database_owner";

-- grant future functions from pg_checkpoint to pg_database_owner in finance_agent (database: finance_agent)
ALTER DEFAULT PRIVILEGES FOR ROLE "pg_checkpoint" GRANT ALL PRIVILEGES ON FUNCTIONS TO "pg_database_owner";

-- grant future schemas from pg_checkpoint to pg_execute_server_program in finance_agent (database: finance_agent)
ALTER DEFAULT PRIVILEGES FOR ROLE "pg_checkpoint" GRANT ALL PRIVILEGES ON SCHEMAS TO "pg_execute_server_program";

-- grant future tables from pg_checkpoint to pg_execute_server_program in finance_agent (database: finance_agent)
ALTER DEFAULT PRIVILEGES FOR ROLE "pg_checkpoint" GRANT ALL PRIVILEGES ON TABLES TO "pg_execute_server_program";

-- grant future sequences from pg_checkpoint to pg_execute_server_program in finance_agent (database: finance_agent)
ALTER DEFAULT PRIVILEGES FOR ROLE "pg_checkpoint" GRANT ALL PRIVILEGES ON SEQUENCES TO "pg_execute_server_program";

-- grant future functions from pg_checkpoint to pg_execute_server_program in finance_agent (database: finance_agent)
ALTER DEFAULT PRIVILEGES FOR ROLE "pg_checkpoint" GRANT ALL PRIVILEGES ON FUNCTIONS TO "pg_execute_server_program";

-- grant future schemas from pg_checkpoint to pg_maintain in finance_agent (database: finance_agent)
ALTER DEFAULT PRIVILEGES FOR ROLE "pg_checkpoint" GRANT ALL PRIVILEGES ON SCHEMAS TO "pg_maintain";

-- grant future tables from pg_checkpoint to pg_maintain in finance_agent (database: finance_agent)
ALTER DEFAULT PRIVILEGES FOR ROLE "pg_checkpoint" GRANT ALL PRIVILEGES ON TABLES TO "pg_maintain";

-- grant future sequences from pg_checkpoint to pg_maintain in finance_agent (database: finance_agent)
ALTER DEFAULT PRIVILEGES FOR ROLE "pg_checkpoint" GRANT ALL PRIVILEGES ON SEQUENCES TO "pg_maintain";

-- grant future functions from pg_checkpoint to pg_maintain in finance_agent (database: finance_agent)
ALTER DEFAULT PRIVILEGES FOR ROLE "pg_checkpoint" GRANT ALL PRIVILEGES ON FUNCTIONS TO "pg_maintain";

-- grant future schemas from pg_checkpoint to pg_monitor in finance_agent (database: finance_agent)
ALTER DEFAULT PRIVILEGES FOR ROLE "pg_checkpoint" GRANT ALL PRIVILEGES ON SCHEMAS TO "pg_monitor";

-- grant future tables from pg_checkpoint to pg_monitor in finance_agent (database: finance_agent)
ALTER DEFAULT PRIVILEGES FOR ROLE "pg_checkpoint" GRANT ALL PRIVILEGES ON TABLES TO "pg_monitor";

-- grant future sequences from pg_checkpoint to pg_monitor in finance_agent (database: finance_agent)
ALTER DEFAULT PRIVILEGES FOR ROLE "pg_checkpoint" GRANT ALL PRIVILEGES ON SEQUENCES TO "pg_monitor";

-- grant future functions from pg_checkpoint to pg_monitor in finance_agent (database: finance_agent)
ALTER DEFAULT PRIVILEGES FOR ROLE "pg_checkpoint" GRANT ALL PRIVILEGES ON FUNCTIONS TO "pg_monitor";

-- grant future schemas from pg_checkpoint to pg_read_all_data in finance_agent (database: finance_agent)
ALTER DEFAULT PRIVILEGES FOR ROLE "pg_checkpoint" GRANT ALL PRIVILEGES ON SCHEMAS TO "pg_read_all_data";

-- grant future tables from pg_checkpoint to pg_read_all_data in finance_agent (database: finance_agent)
ALTER DEFAULT PRIVILEGES FOR ROLE "pg_checkpoint" GRANT ALL PRIVILEGES ON TABLES TO "pg_read_all_data";

-- grant future sequences from pg_checkpoint to pg_read_all_data in finance_agent (database: finance_agent)
ALTER DEFAULT PRIVILEGES FOR ROLE "pg_checkpoint" GRANT ALL PRIVILEGES ON SEQUENCES TO "pg_read_all_data";

-- grant future functions from pg_checkpoint to pg_read_all_data in finance_agent (database: finance_agent)
ALTER DEFAULT PRIVILEGES FOR ROLE "pg_checkpoint" GRANT ALL PRIVILEGES ON FUNCTIONS TO "pg_read_all_data";

-- grant future schemas from pg_checkpoint to pg_read_all_stats in finance_agent (database: finance_agent)
ALTER DEFAULT PRIVILEGES FOR ROLE "pg_checkpoint" GRANT ALL PRIVILEGES ON SCHEMAS TO "pg_read_all_stats";

-- grant future tables from pg_checkpoint to pg_read_all_stats in finance_agent (database: finance_agent)
ALTER DEFAULT PRIVILEGES FOR ROLE "pg_checkpoint" GRANT ALL PRIVILEGES ON TABLES TO "pg_read_all_stats";

-- grant future sequences from pg_checkpoint to pg_read_all_stats in finance_agent (database: finance_agent)
ALTER DEFAULT PRIVILEGES FOR ROLE "pg_checkpoint" GRANT ALL PRIVILEGES ON SEQUENCES TO "pg_read_all_stats";

-- grant future functions from pg_checkpoint to pg_read_all_stats in finance_agent (database: finance_agent)
ALTER DEFAULT PRIVILEGES FOR ROLE "pg_checkpoint" GRANT ALL PRIVILEGES ON FUNCTIONS TO "pg_read_all_stats";

-- grant future schemas from pg_checkpoint to pg_read_all_settings in finance_agent (database: finance_agent)
ALTER DEFAULT PRIVILEGES FOR ROLE "pg_checkpoint" GRANT ALL PRIVILEGES ON SCHEMAS TO "pg_read_all_settings";

-- grant future tables from pg_checkpoint to pg_read_all_settings in finance_agent (database: finance_agent)
ALTER DEFAULT PRIVILEGES FOR ROLE "pg_checkpoint" GRANT ALL PRIVILEGES ON TABLES TO "pg_read_all_settings";

-- grant future sequences from pg_checkpoint to pg_read_all_settings in finance_agent (database: finance_agent)
ALTER DEFAULT PRIVILEGES FOR ROLE "pg_checkpoint" GRANT ALL PRIVILEGES ON SEQUENCES TO "pg_read_all_settings";

-- grant future functions from pg_checkpoint to pg_read_all_settings in finance_agent (database: finance_agent)
ALTER DEFAULT PRIVILEGES FOR ROLE "pg_checkpoint" GRANT ALL PRIVILEGES ON FUNCTIONS TO "pg_read_all_settings";

-- grant future schemas from pg_checkpoint to pg_read_server_files in finance_agent (database: finance_agent)
ALTER DEFAULT PRIVILEGES FOR ROLE "pg_checkpoint" GRANT ALL PRIVILEGES ON SCHEMAS TO "pg_read_server_files";

-- grant future tables from pg_checkpoint to pg_read_server_files in finance_agent (database: finance_agent)
ALTER DEFAULT PRIVILEGES FOR ROLE "pg_checkpoint" GRANT ALL PRIVILEGES ON TABLES TO "pg_read_server_files";

-- grant future sequences from pg_checkpoint to pg_read_server_files in finance_agent (database: finance_agent)
ALTER DEFAULT PRIVILEGES FOR ROLE "pg_checkpoint" GRANT ALL PRIVILEGES ON SEQUENCES TO "pg_read_server_files";

-- grant future functions from pg_checkpoint to pg_read_server_files in finance_agent (database: finance_agent)
ALTER DEFAULT PRIVILEGES FOR ROLE "pg_checkpoint" GRANT ALL PRIVILEGES ON FUNCTIONS TO "pg_read_server_files";

-- grant future schemas from pg_checkpoint to pg_signal_backend in finance_agent (database: finance_agent)
ALTER DEFAULT PRIVILEGES FOR ROLE "pg_checkpoint" GRANT ALL PRIVILEGES ON SCHEMAS TO "pg_signal_backend";

-- grant future tables from pg_checkpoint to pg_signal_backend in finance_agent (database: finance_agent)
ALTER DEFAULT PRIVILEGES FOR ROLE "pg_checkpoint" GRANT ALL PRIVILEGES ON TABLES TO "pg_signal_backend";

-- grant future sequences from pg_checkpoint to pg_signal_backend in finance_agent (database: finance_agent)
ALTER DEFAULT PRIVILEGES FOR ROLE "pg_checkpoint" GRANT ALL PRIVILEGES ON SEQUENCES TO "pg_signal_backend";

-- grant future functions from pg_checkpoint to pg_signal_backend in finance_agent (database: finance_agent)
ALTER DEFAULT PRIVILEGES FOR ROLE "pg_checkpoint" GRANT ALL PRIVILEGES ON FUNCTIONS TO "pg_signal_backend";

-- grant future schemas from pg_checkpoint to pg_stat_scan_tables in finance_agent (database: finance_agent)
ALTER DEFAULT PRIVILEGES FOR ROLE "pg_checkpoint" GRANT ALL PRIVILEGES ON SCHEMAS TO "pg_stat_scan_tables";

-- grant future tables from pg_checkpoint to pg_stat_scan_tables in finance_agent (database: finance_agent)
ALTER DEFAULT PRIVILEGES FOR ROLE "pg_checkpoint" GRANT ALL PRIVILEGES ON TABLES TO "pg_stat_scan_tables";

-- grant future sequences from pg_checkpoint to pg_stat_scan_tables in finance_agent (database: finance_agent)
ALTER DEFAULT PRIVILEGES FOR ROLE "pg_checkpoint" GRANT ALL PRIVILEGES ON SEQUENCES TO "pg_stat_scan_tables";

-- grant future functions from pg_checkpoint to pg_stat_scan_tables in finance_agent (database: finance_agent)
ALTER DEFAULT PRIVILEGES FOR ROLE "pg_checkpoint" GRANT ALL PRIVILEGES ON FUNCTIONS TO "pg_stat_scan_tables";

-- grant future schemas from pg_checkpoint to pg_use_reserved_connections in finance_agent (database: finance_agent)
ALTER DEFAULT PRIVILEGES FOR ROLE "pg_checkpoint" GRANT ALL PRIVILEGES ON SCHEMAS TO "pg_use_reserved_connections";

-- grant future tables from pg_checkpoint to pg_use_reserved_connections in finance_agent (database: finance_agent)
ALTER DEFAULT PRIVILEGES FOR ROLE "pg_checkpoint" GRANT ALL PRIVILEGES ON TABLES TO "pg_use_reserved_connections";

-- grant future sequences from pg_checkpoint to pg_use_reserved_connections in finance_agent (database: finance_agent)
ALTER DEFAULT PRIVILEGES FOR ROLE "pg_checkpoint" GRANT ALL PRIVILEGES ON SEQUENCES TO "pg_use_reserved_connections";

-- grant future functions from pg_checkpoint to pg_use_reserved_connections in finance_agent (database: finance_agent)
ALTER DEFAULT PRIVILEGES FOR ROLE "pg_checkpoint" GRANT ALL PRIVILEGES ON FUNCTIONS TO "pg_use_reserved_connections";

-- grant future schemas from pg_checkpoint to pg_write_all_data in finance_agent (database: finance_agent)
ALTER DEFAULT PRIVILEGES FOR ROLE "pg_checkpoint" GRANT ALL PRIVILEGES ON SCHEMAS TO "pg_write_all_data";

-- grant future tables from pg_checkpoint to pg_write_all_data in finance_agent (database: finance_agent)
ALTER DEFAULT PRIVILEGES FOR ROLE "pg_checkpoint" GRANT ALL PRIVILEGES ON TABLES TO "pg_write_all_data";

-- grant future sequences from pg_checkpoint to pg_write_all_data in finance_agent (database: finance_agent)
ALTER DEFAULT PRIVILEGES FOR ROLE "pg_checkpoint" GRANT ALL PRIVILEGES ON SEQUENCES TO "pg_write_all_data";

-- grant future functions from pg_checkpoint to pg_write_all_data in finance_agent (database: finance_agent)
ALTER DEFAULT PRIVILEGES FOR ROLE "pg_checkpoint" GRANT ALL PRIVILEGES ON FUNCTIONS TO "pg_write_all_data";

-- grant future schemas from pg_checkpoint to pg_write_server_files in finance_agent (database: finance_agent)
ALTER DEFAULT PRIVILEGES FOR ROLE "pg_checkpoint" GRANT ALL PRIVILEGES ON SCHEMAS TO "pg_write_server_files";

-- grant future tables from pg_checkpoint to pg_write_server_files in finance_agent (database: finance_agent)
ALTER DEFAULT PRIVILEGES FOR ROLE "pg_checkpoint" GRANT ALL PRIVILEGES ON TABLES TO "pg_write_server_files";

-- grant future sequences from pg_checkpoint to pg_write_server_files in finance_agent (database: finance_agent)
ALTER DEFAULT PRIVILEGES FOR ROLE "pg_checkpoint" GRANT ALL PRIVILEGES ON SEQUENCES TO "pg_write_server_files";

-- grant future functions from pg_checkpoint to pg_write_server_files in finance_agent (database: finance_agent)
ALTER DEFAULT PRIVILEGES FOR ROLE "pg_checkpoint" GRANT ALL PRIVILEGES ON FUNCTIONS TO "pg_write_server_files";

-- grant future schemas from pg_checkpoint to postgres in finance_agent (database: finance_agent)
ALTER DEFAULT PRIVILEGES FOR ROLE "pg_checkpoint" GRANT ALL PRIVILEGES ON SCHEMAS TO "postgres";

-- grant future tables from pg_checkpoint to postgres in finance_agent (database: finance_agent)
ALTER DEFAULT PRIVILEGES FOR ROLE "pg_checkpoint" GRANT ALL PRIVILEGES ON TABLES TO "postgres";

-- grant future sequences from pg_checkpoint to postgres in finance_agent (database: finance_agent)
ALTER DEFAULT PRIVILEGES FOR ROLE "pg_checkpoint" GRANT ALL PRIVILEGES ON SEQUENCES TO "postgres";

-- grant future functions from pg_checkpoint to postgres in finance_agent (database: finance_agent)
ALTER DEFAULT PRIVILEGES FOR ROLE "pg_checkpoint" GRANT ALL PRIVILEGES ON FUNCTIONS TO "postgres";

-- grant future schemas from pg_create_subscription to pg_checkpoint in finance_agent (database: finance_agent)
ALTER DEFAULT PRIVILEGES FOR ROLE "pg_create_subscription" GRANT ALL PRIVILEGES ON SCHEMAS TO "pg_checkpoint";

-- grant future tables from pg_create_subscription to pg_checkpoint in finance_agent (database: finance_agent)
ALTER DEFAULT PRIVILEGES FOR ROLE "pg_create_subscription" GRANT ALL PRIVILEGES ON TABLES TO "pg_checkpoint";

-- grant future sequences from pg_create_subscription to pg_checkpoint in finance_agent (database: finance_agent)
ALTER DEFAULT PRIVILEGES FOR ROLE "pg_create_subscription" GRANT ALL PRIVILEGES ON SEQUENCES TO "pg_checkpoint";

-- grant future functions from pg_create_subscription to pg_checkpoint in finance_agent (database: finance_agent)
ALTER DEFAULT PRIVILEGES FOR ROLE "pg_create_subscription" GRANT ALL PRIVILEGES ON FUNCTIONS TO "pg_checkpoint";

-- grant future schemas from pg_create_subscription to pg_database_owner in finance_agent (database: finance_agent)
ALTER DEFAULT PRIVILEGES FOR ROLE "pg_create_subscription" GRANT ALL PRIVILEGES ON SCHEMAS TO "pg_database_owner";

-- grant future tables from pg_create_subscription to pg_database_owner in finance_agent (database: finance_agent)
ALTER DEFAULT PRIVILEGES FOR ROLE "pg_create_subscription" GRANT ALL PRIVILEGES ON TABLES TO "pg_database_owner";

-- grant future sequences from pg_create_subscription to pg_database_owner in finance_agent (database: finance_agent)
ALTER DEFAULT PRIVILEGES FOR ROLE "pg_create_subscription" GRANT ALL PRIVILEGES ON SEQUENCES TO "pg_database_owner";

-- grant future functions from pg_create_subscription to pg_database_owner in finance_agent (database: finance_agent)
ALTER DEFAULT PRIVILEGES FOR ROLE "pg_create_subscription" GRANT ALL PRIVILEGES ON FUNCTIONS TO "pg_database_owner";

-- grant future schemas from pg_create_subscription to pg_execute_server_program in finance_agent (database: finance_agent)
ALTER DEFAULT PRIVILEGES FOR ROLE "pg_create_subscription" GRANT ALL PRIVILEGES ON SCHEMAS TO "pg_execute_server_program";

-- grant future tables from pg_create_subscription to pg_execute_server_program in finance_agent (database: finance_agent)
ALTER DEFAULT PRIVILEGES FOR ROLE "pg_create_subscription" GRANT ALL PRIVILEGES ON TABLES TO "pg_execute_server_program";

-- grant future sequences from pg_create_subscription to pg_execute_server_program in finance_agent (database: finance_agent)
ALTER DEFAULT PRIVILEGES FOR ROLE "pg_create_subscription" GRANT ALL PRIVILEGES ON SEQUENCES TO "pg_execute_server_program";

-- grant future functions from pg_create_subscription to pg_execute_server_program in finance_agent (database: finance_agent)
ALTER DEFAULT PRIVILEGES FOR ROLE "pg_create_subscription" GRANT ALL PRIVILEGES ON FUNCTIONS TO "pg_execute_server_program";

-- grant future schemas from pg_create_subscription to pg_maintain in finance_agent (database: finance_agent)
ALTER DEFAULT PRIVILEGES FOR ROLE "pg_create_subscription" GRANT ALL PRIVILEGES ON SCHEMAS TO "pg_maintain";

-- grant future tables from pg_create_subscription to pg_maintain in finance_agent (database: finance_agent)
ALTER DEFAULT PRIVILEGES FOR ROLE "pg_create_subscription" GRANT ALL PRIVILEGES ON TABLES TO "pg_maintain";

-- grant future sequences from pg_create_subscription to pg_maintain in finance_agent (database: finance_agent)
ALTER DEFAULT PRIVILEGES FOR ROLE "pg_create_subscription" GRANT ALL PRIVILEGES ON SEQUENCES TO "pg_maintain";

-- grant future functions from pg_create_subscription to pg_maintain in finance_agent (database: finance_agent)
ALTER DEFAULT PRIVILEGES FOR ROLE "pg_create_subscription" GRANT ALL PRIVILEGES ON FUNCTIONS TO "pg_maintain";

-- grant future schemas from pg_create_subscription to pg_monitor in finance_agent (database: finance_agent)
ALTER DEFAULT PRIVILEGES FOR ROLE "pg_create_subscription" GRANT ALL PRIVILEGES ON SCHEMAS TO "pg_monitor";

-- grant future tables from pg_create_subscription to pg_monitor in finance_agent (database: finance_agent)
ALTER DEFAULT PRIVILEGES FOR ROLE "pg_create_subscription" GRANT ALL PRIVILEGES ON TABLES TO "pg_monitor";

-- grant future sequences from pg_create_subscription to pg_monitor in finance_agent (database: finance_agent)
ALTER DEFAULT PRIVILEGES FOR ROLE "pg_create_subscription" GRANT ALL PRIVILEGES ON SEQUENCES TO "pg_monitor";

-- grant future functions from pg_create_subscription to pg_monitor in finance_agent (database: finance_agent)
ALTER DEFAULT PRIVILEGES FOR ROLE "pg_create_subscription" GRANT ALL PRIVILEGES ON FUNCTIONS TO "pg_monitor";

-- grant future schemas from pg_create_subscription to pg_read_all_data in finance_agent (database: finance_agent)
ALTER DEFAULT PRIVILEGES FOR ROLE "pg_create_subscription" GRANT ALL PRIVILEGES ON SCHEMAS TO "pg_read_all_data";

-- grant future tables from pg_create_subscription to pg_read_all_data in finance_agent (database: finance_agent)
ALTER DEFAULT PRIVILEGES FOR ROLE "pg_create_subscription" GRANT ALL PRIVILEGES ON TABLES TO "pg_read_all_data";

-- grant future sequences from pg_create_subscription to pg_read_all_data in finance_agent (database: finance_agent)
ALTER DEFAULT PRIVILEGES FOR ROLE "pg_create_subscription" GRANT ALL PRIVILEGES ON SEQUENCES TO "pg_read_all_data";

-- grant future functions from pg_create_subscription to pg_read_all_data in finance_agent (database: finance_agent)
ALTER DEFAULT PRIVILEGES FOR ROLE "pg_create_subscription" GRANT ALL PRIVILEGES ON FUNCTIONS TO "pg_read_all_data";

-- grant future schemas from pg_create_subscription to pg_read_all_stats in finance_agent (database: finance_agent)
ALTER DEFAULT PRIVILEGES FOR ROLE "pg_create_subscription" GRANT ALL PRIVILEGES ON SCHEMAS TO "pg_read_all_stats";

-- grant future tables from pg_create_subscription to pg_read_all_stats in finance_agent (database: finance_agent)
ALTER DEFAULT PRIVILEGES FOR ROLE "pg_create_subscription" GRANT ALL PRIVILEGES ON TABLES TO "pg_read_all_stats";

-- grant future sequences from pg_create_subscription to pg_read_all_stats in finance_agent (database: finance_agent)
ALTER DEFAULT PRIVILEGES FOR ROLE "pg_create_subscription" GRANT ALL PRIVILEGES ON SEQUENCES TO "pg_read_all_stats";

-- grant future functions from pg_create_subscription to pg_read_all_stats in finance_agent (database: finance_agent)
ALTER DEFAULT PRIVILEGES FOR ROLE "pg_create_subscription" GRANT ALL PRIVILEGES ON FUNCTIONS TO "pg_read_all_stats";

-- grant future schemas from pg_create_subscription to pg_read_all_settings in finance_agent (database: finance_agent)
ALTER DEFAULT PRIVILEGES FOR ROLE "pg_create_subscription" GRANT ALL PRIVILEGES ON SCHEMAS TO "pg_read_all_settings";

-- grant future tables from pg_create_subscription to pg_read_all_settings in finance_agent (database: finance_agent)
ALTER DEFAULT PRIVILEGES FOR ROLE "pg_create_subscription" GRANT ALL PRIVILEGES ON TABLES TO "pg_read_all_settings";

-- grant future sequences from pg_create_subscription to pg_read_all_settings in finance_agent (database: finance_agent)
ALTER DEFAULT PRIVILEGES FOR ROLE "pg_create_subscription" GRANT ALL PRIVILEGES ON SEQUENCES TO "pg_read_all_settings";

-- grant future functions from pg_create_subscription to pg_read_all_settings in finance_agent (database: finance_agent)
ALTER DEFAULT PRIVILEGES FOR ROLE "pg_create_subscription" GRANT ALL PRIVILEGES ON FUNCTIONS TO "pg_read_all_settings";

-- grant future schemas from pg_create_subscription to pg_read_server_files in finance_agent (database: finance_agent)
ALTER DEFAULT PRIVILEGES FOR ROLE "pg_create_subscription" GRANT ALL PRIVILEGES ON SCHEMAS TO "pg_read_server_files";

-- grant future tables from pg_create_subscription to pg_read_server_files in finance_agent (database: finance_agent)
ALTER DEFAULT PRIVILEGES FOR ROLE "pg_create_subscription" GRANT ALL PRIVILEGES ON TABLES TO "pg_read_server_files";

-- grant future sequences from pg_create_subscription to pg_read_server_files in finance_agent (database: finance_agent)
ALTER DEFAULT PRIVILEGES FOR ROLE "pg_create_subscription" GRANT ALL PRIVILEGES ON SEQUENCES TO "pg_read_server_files";

-- grant future functions from pg_create_subscription to pg_read_server_files in finance_agent (database: finance_agent)
ALTER DEFAULT PRIVILEGES FOR ROLE "pg_create_subscription" GRANT ALL PRIVILEGES ON FUNCTIONS TO "pg_read_server_files";

-- grant future schemas from pg_create_subscription to pg_signal_backend in finance_agent (database: finance_agent)
ALTER DEFAULT PRIVILEGES FOR ROLE "pg_create_subscription" GRANT ALL PRIVILEGES ON SCHEMAS TO "pg_signal_backend";

-- grant future tables from pg_create_subscription to pg_signal_backend in finance_agent (database: finance_agent)
ALTER DEFAULT PRIVILEGES FOR ROLE "pg_create_subscription" GRANT ALL PRIVILEGES ON TABLES TO "pg_signal_backend";

-- grant future sequences from pg_create_subscription to pg_signal_backend in finance_agent (database: finance_agent)
ALTER DEFAULT PRIVILEGES FOR ROLE "pg_create_subscription" GRANT ALL PRIVILEGES ON SEQUENCES TO "pg_signal_backend";

-- grant future functions from pg_create_subscription to pg_signal_backend in finance_agent (database: finance_agent)
ALTER DEFAULT PRIVILEGES FOR ROLE "pg_create_subscription" GRANT ALL PRIVILEGES ON FUNCTIONS TO "pg_signal_backend";

-- grant future schemas from pg_create_subscription to pg_stat_scan_tables in finance_agent (database: finance_agent)
ALTER DEFAULT PRIVILEGES FOR ROLE "pg_create_subscription" GRANT ALL PRIVILEGES ON SCHEMAS TO "pg_stat_scan_tables";

-- grant future tables from pg_create_subscription to pg_stat_scan_tables in finance_agent (database: finance_agent)
ALTER DEFAULT PRIVILEGES FOR ROLE "pg_create_subscription" GRANT ALL PRIVILEGES ON TABLES TO "pg_stat_scan_tables";

-- grant future sequences from pg_create_subscription to pg_stat_scan_tables in finance_agent (database: finance_agent)
ALTER DEFAULT PRIVILEGES FOR ROLE "pg_create_subscription" GRANT ALL PRIVILEGES ON SEQUENCES TO "pg_stat_scan_tables";

-- grant future functions from pg_create_subscription to pg_stat_scan_tables in finance_agent (database: finance_agent)
ALTER DEFAULT PRIVILEGES FOR ROLE "pg_create_subscription" GRANT ALL PRIVILEGES ON FUNCTIONS TO "pg_stat_scan_tables";

-- grant future schemas from pg_create_subscription to pg_use_reserved_connections in finance_agent (database: finance_agent)
ALTER DEFAULT PRIVILEGES FOR ROLE "pg_create_subscription" GRANT ALL PRIVILEGES ON SCHEMAS TO "pg_use_reserved_connections";

-- grant future tables from pg_create_subscription to pg_use_reserved_connections in finance_agent (database: finance_agent)
ALTER DEFAULT PRIVILEGES FOR ROLE "pg_create_subscription" GRANT ALL PRIVILEGES ON TABLES TO "pg_use_reserved_connections";

-- grant future sequences from pg_create_subscription to pg_use_reserved_connections in finance_agent (database: finance_agent)
ALTER DEFAULT PRIVILEGES FOR ROLE "pg_create_subscription" GRANT ALL PRIVILEGES ON SEQUENCES TO "pg_use_reserved_connections";

-- grant future functions from pg_create_subscription to pg_use_reserved_connections in finance_agent (database: finance_agent)
ALTER DEFAULT PRIVILEGES FOR ROLE "pg_create_subscription" GRANT ALL PRIVILEGES ON FUNCTIONS TO "pg_use_reserved_connections";

-- grant future schemas from pg_create_subscription to pg_write_all_data in finance_agent (database: finance_agent)
ALTER DEFAULT PRIVILEGES FOR ROLE "pg_create_subscription" GRANT ALL PRIVILEGES ON SCHEMAS TO "pg_write_all_data";

-- grant future tables from pg_create_subscription to pg_write_all_data in finance_agent (database: finance_agent)
ALTER DEFAULT PRIVILEGES FOR ROLE "pg_create_subscription" GRANT ALL PRIVILEGES ON TABLES TO "pg_write_all_data";

-- grant future sequences from pg_create_subscription to pg_write_all_data in finance_agent (database: finance_agent)
ALTER DEFAULT PRIVILEGES FOR ROLE "pg_create_subscription" GRANT ALL PRIVILEGES ON SEQUENCES TO "pg_write_all_data";

-- grant future functions from pg_create_subscription to pg_write_all_data in finance_agent (database: finance_agent)
ALTER DEFAULT PRIVILEGES FOR ROLE "pg_create_subscription" GRANT ALL PRIVILEGES ON FUNCTIONS TO "pg_write_all_data";

-- grant future schemas from pg_create_subscription to pg_write_server_files in finance_agent (database: finance_agent)
ALTER DEFAULT PRIVILEGES FOR ROLE "pg_create_subscription" GRANT ALL PRIVILEGES ON SCHEMAS TO "pg_write_server_files";

-- grant future tables from pg_create_subscription to pg_write_server_files in finance_agent (database: finance_agent)
ALTER DEFAULT PRIVILEGES FOR ROLE "pg_create_subscription" GRANT ALL PRIVILEGES ON TABLES TO "pg_write_server_files";

-- grant future sequences from pg_create_subscription to pg_write_server_files in finance_agent (database: finance_agent)
ALTER DEFAULT PRIVILEGES FOR ROLE "pg_create_subscription" GRANT ALL PRIVILEGES ON SEQUENCES TO "pg_write_server_files";

-- grant future functions from pg_create_subscription to pg_write_server_files in finance_agent (database: finance_agent)
ALTER DEFAULT PRIVILEGES FOR ROLE "pg_create_subscription" GRANT ALL PRIVILEGES ON FUNCTIONS TO "pg_write_server_files";

-- grant future schemas from pg_create_subscription to postgres in finance_agent (database: finance_agent)
ALTER DEFAULT PRIVILEGES FOR ROLE "pg_create_subscription" GRANT ALL PRIVILEGES ON SCHEMAS TO "postgres";

-- grant future tables from pg_create_subscription to postgres in finance_agent (database: finance_agent)
ALTER DEFAULT PRIVILEGES FOR ROLE "pg_create_subscription" GRANT ALL PRIVILEGES ON TABLES TO "postgres";

-- grant future sequences from pg_create_subscription to postgres in finance_agent (database: finance_agent)
ALTER DEFAULT PRIVILEGES FOR ROLE "pg_create_subscription" GRANT ALL PRIVILEGES ON SEQUENCES TO "postgres";

-- grant future functions from pg_create_subscription to postgres in finance_agent (database: finance_agent)
ALTER DEFAULT PRIVILEGES FOR ROLE "pg_create_subscription" GRANT ALL PRIVILEGES ON FUNCTIONS TO "postgres";

-- grant future schemas from pg_database_owner to pg_checkpoint in finance_agent (database: finance_agent)
ALTER DEFAULT PRIVILEGES FOR ROLE "pg_database_owner" GRANT ALL PRIVILEGES ON SCHEMAS TO "pg_checkpoint";

-- grant future tables from pg_database_owner to pg_checkpoint in finance_agent (database: finance_agent)
ALTER DEFAULT PRIVILEGES FOR ROLE "pg_database_owner" GRANT ALL PRIVILEGES ON TABLES TO "pg_checkpoint";

-- grant future sequences from pg_database_owner to pg_checkpoint in finance_agent (database: finance_agent)
ALTER DEFAULT PRIVILEGES FOR ROLE "pg_database_owner" GRANT ALL PRIVILEGES ON SEQUENCES TO "pg_checkpoint";

-- grant future functions from pg_database_owner to pg_checkpoint in finance_agent (database: finance_agent)
ALTER DEFAULT PRIVILEGES FOR ROLE "pg_database_owner" GRANT ALL PRIVILEGES ON FUNCTIONS TO "pg_checkpoint";

-- grant future schemas from pg_database_owner to pg_create_subscription in finance_agent (database: finance_agent)
ALTER DEFAULT PRIVILEGES FOR ROLE "pg_database_owner" GRANT ALL PRIVILEGES ON SCHEMAS TO "pg_create_subscription";

-- grant future tables from pg_database_owner to pg_create_subscription in finance_agent (database: finance_agent)
ALTER DEFAULT PRIVILEGES FOR ROLE "pg_database_owner" GRANT ALL PRIVILEGES ON TABLES TO "pg_create_subscription";

-- grant future sequences from pg_database_owner to pg_create_subscription in finance_agent (database: finance_agent)
ALTER DEFAULT PRIVILEGES FOR ROLE "pg_database_owner" GRANT ALL PRIVILEGES ON SEQUENCES TO "pg_create_subscription";

-- grant future functions from pg_database_owner to pg_create_subscription in finance_agent (database: finance_agent)
ALTER DEFAULT PRIVILEGES FOR ROLE "pg_database_owner" GRANT ALL PRIVILEGES ON FUNCTIONS TO "pg_create_subscription";

-- grant future schemas from pg_database_owner to pg_execute_server_program in finance_agent (database: finance_agent)
ALTER DEFAULT PRIVILEGES FOR ROLE "pg_database_owner" GRANT ALL PRIVILEGES ON SCHEMAS TO "pg_execute_server_program";

-- grant future tables from pg_database_owner to pg_execute_server_program in finance_agent (database: finance_agent)
ALTER DEFAULT PRIVILEGES FOR ROLE "pg_database_owner" GRANT ALL PRIVILEGES ON TABLES TO "pg_execute_server_program";

-- grant future sequences from pg_database_owner to pg_execute_server_program in finance_agent (database: finance_agent)
ALTER DEFAULT PRIVILEGES FOR ROLE "pg_database_owner" GRANT ALL PRIVILEGES ON SEQUENCES TO "pg_execute_server_program";

-- grant future functions from pg_database_owner to pg_execute_server_program in finance_agent (database: finance_agent)
ALTER DEFAULT PRIVILEGES FOR ROLE "pg_database_owner" GRANT ALL PRIVILEGES ON FUNCTIONS TO "pg_execute_server_program";

-- grant future schemas from pg_database_owner to pg_maintain in finance_agent (database: finance_agent)
ALTER DEFAULT PRIVILEGES FOR ROLE "pg_database_owner" GRANT ALL PRIVILEGES ON SCHEMAS TO "pg_maintain";

-- grant future tables from pg_database_owner to pg_maintain in finance_agent (database: finance_agent)
ALTER DEFAULT PRIVILEGES FOR ROLE "pg_database_owner" GRANT ALL PRIVILEGES ON TABLES TO "pg_maintain";

-- grant future sequences from pg_database_owner to pg_maintain in finance_agent (database: finance_agent)
ALTER DEFAULT PRIVILEGES FOR ROLE "pg_database_owner" GRANT ALL PRIVILEGES ON SEQUENCES TO "pg_maintain";

-- grant future functions from pg_database_owner to pg_maintain in finance_agent (database: finance_agent)
ALTER DEFAULT PRIVILEGES FOR ROLE "pg_database_owner" GRANT ALL PRIVILEGES ON FUNCTIONS TO "pg_maintain";

-- grant future schemas from pg_database_owner to pg_monitor in finance_agent (database: finance_agent)
ALTER DEFAULT PRIVILEGES FOR ROLE "pg_database_owner" GRANT ALL PRIVILEGES ON SCHEMAS TO "pg_monitor";

-- grant future tables from pg_database_owner to pg_monitor in finance_agent (database: finance_agent)
ALTER DEFAULT PRIVILEGES FOR ROLE "pg_database_owner" GRANT ALL PRIVILEGES ON TABLES TO "pg_monitor";

-- grant future sequences from pg_database_owner to pg_monitor in finance_agent (database: finance_agent)
ALTER DEFAULT PRIVILEGES FOR ROLE "pg_database_owner" GRANT ALL PRIVILEGES ON SEQUENCES TO "pg_monitor";

-- grant future functions from pg_database_owner to pg_monitor in finance_agent (database: finance_agent)
ALTER DEFAULT PRIVILEGES FOR ROLE "pg_database_owner" GRANT ALL PRIVILEGES ON FUNCTIONS TO "pg_monitor";

-- grant future schemas from pg_database_owner to pg_read_all_data in finance_agent (database: finance_agent)
ALTER DEFAULT PRIVILEGES FOR ROLE "pg_database_owner" GRANT ALL PRIVILEGES ON SCHEMAS TO "pg_read_all_data";

-- grant future tables from pg_database_owner to pg_read_all_data in finance_agent (database: finance_agent)
ALTER DEFAULT PRIVILEGES FOR ROLE "pg_database_owner" GRANT ALL PRIVILEGES ON TABLES TO "pg_read_all_data";

-- grant future sequences from pg_database_owner to pg_read_all_data in finance_agent (database: finance_agent)
ALTER DEFAULT PRIVILEGES FOR ROLE "pg_database_owner" GRANT ALL PRIVILEGES ON SEQUENCES TO "pg_read_all_data";

-- grant future functions from pg_database_owner to pg_read_all_data in finance_agent (database: finance_agent)
ALTER DEFAULT PRIVILEGES FOR ROLE "pg_database_owner" GRANT ALL PRIVILEGES ON FUNCTIONS TO "pg_read_all_data";

-- grant future schemas from pg_database_owner to pg_read_all_stats in finance_agent (database: finance_agent)
ALTER DEFAULT PRIVILEGES FOR ROLE "pg_database_owner" GRANT ALL PRIVILEGES ON SCHEMAS TO "pg_read_all_stats";

-- grant future tables from pg_database_owner to pg_read_all_stats in finance_agent (database: finance_agent)
ALTER DEFAULT PRIVILEGES FOR ROLE "pg_database_owner" GRANT ALL PRIVILEGES ON TABLES TO "pg_read_all_stats";

-- grant future sequences from pg_database_owner to pg_read_all_stats in finance_agent (database: finance_agent)
ALTER DEFAULT PRIVILEGES FOR ROLE "pg_database_owner" GRANT ALL PRIVILEGES ON SEQUENCES TO "pg_read_all_stats";

-- grant future functions from pg_database_owner to pg_read_all_stats in finance_agent (database: finance_agent)
ALTER DEFAULT PRIVILEGES FOR ROLE "pg_database_owner" GRANT ALL PRIVILEGES ON FUNCTIONS TO "pg_read_all_stats";

-- grant future schemas from pg_database_owner to pg_read_all_settings in finance_agent (database: finance_agent)
ALTER DEFAULT PRIVILEGES FOR ROLE "pg_database_owner" GRANT ALL PRIVILEGES ON SCHEMAS TO "pg_read_all_settings";

-- grant future tables from pg_database_owner to pg_read_all_settings in finance_agent (database: finance_agent)
ALTER DEFAULT PRIVILEGES FOR ROLE "pg_database_owner" GRANT ALL PRIVILEGES ON TABLES TO "pg_read_all_settings";

-- grant future sequences from pg_database_owner to pg_read_all_settings in finance_agent (database: finance_agent)
ALTER DEFAULT PRIVILEGES FOR ROLE "pg_database_owner" GRANT ALL PRIVILEGES ON SEQUENCES TO "pg_read_all_settings";

-- grant future functions from pg_database_owner to pg_read_all_settings in finance_agent (database: finance_agent)
ALTER DEFAULT PRIVILEGES FOR ROLE "pg_database_owner" GRANT ALL PRIVILEGES ON FUNCTIONS TO "pg_read_all_settings";

-- grant future schemas from pg_database_owner to pg_read_server_files in finance_agent (database: finance_agent)
ALTER DEFAULT PRIVILEGES FOR ROLE "pg_database_owner" GRANT ALL PRIVILEGES ON SCHEMAS TO "pg_read_server_files";

-- grant future tables from pg_database_owner to pg_read_server_files in finance_agent (database: finance_agent)
ALTER DEFAULT PRIVILEGES FOR ROLE "pg_database_owner" GRANT ALL PRIVILEGES ON TABLES TO "pg_read_server_files";

-- grant future sequences from pg_database_owner to pg_read_server_files in finance_agent (database: finance_agent)
ALTER DEFAULT PRIVILEGES FOR ROLE "pg_database_owner" GRANT ALL PRIVILEGES ON SEQUENCES TO "pg_read_server_files";

-- grant future functions from pg_database_owner to pg_read_server_files in finance_agent (database: finance_agent)
ALTER DEFAULT PRIVILEGES FOR ROLE "pg_database_owner" GRANT ALL PRIVILEGES ON FUNCTIONS TO "pg_read_server_files";

-- grant future schemas from pg_database_owner to pg_signal_backend in finance_agent (database: finance_agent)
ALTER DEFAULT PRIVILEGES FOR ROLE "pg_database_owner" GRANT ALL PRIVILEGES ON SCHEMAS TO "pg_signal_backend";

-- grant future tables from pg_database_owner to pg_signal_backend in finance_agent (database: finance_agent)
ALTER DEFAULT PRIVILEGES FOR ROLE "pg_database_owner" GRANT ALL PRIVILEGES ON TABLES TO "pg_signal_backend";

-- grant future sequences from pg_database_owner to pg_signal_backend in finance_agent (database: finance_agent)
ALTER DEFAULT PRIVILEGES FOR ROLE "pg_database_owner" GRANT ALL PRIVILEGES ON SEQUENCES TO "pg_signal_backend";

-- grant future functions from pg_database_owner to pg_signal_backend in finance_agent (database: finance_agent)
ALTER DEFAULT PRIVILEGES FOR ROLE "pg_database_owner" GRANT ALL PRIVILEGES ON FUNCTIONS TO "pg_signal_backend";

-- grant future schemas from pg_database_owner to pg_stat_scan_tables in finance_agent (database: finance_agent)
ALTER DEFAULT PRIVILEGES FOR ROLE "pg_database_owner" GRANT ALL PRIVILEGES ON SCHEMAS TO "pg_stat_scan_tables";

-- grant future tables from pg_database_owner to pg_stat_scan_tables in finance_agent (database: finance_agent)
ALTER DEFAULT PRIVILEGES FOR ROLE "pg_database_owner" GRANT ALL PRIVILEGES ON TABLES TO "pg_stat_scan_tables";

-- grant future sequences from pg_database_owner to pg_stat_scan_tables in finance_agent (database: finance_agent)
ALTER DEFAULT PRIVILEGES FOR ROLE "pg_database_owner" GRANT ALL PRIVILEGES ON SEQUENCES TO "pg_stat_scan_tables";

-- grant future functions from pg_database_owner to pg_stat_scan_tables in finance_agent (database: finance_agent)
ALTER DEFAULT PRIVILEGES FOR ROLE "pg_database_owner" GRANT ALL PRIVILEGES ON FUNCTIONS TO "pg_stat_scan_tables";

-- grant future schemas from pg_database_owner to pg_use_reserved_connections in finance_agent (database: finance_agent)
ALTER DEFAULT PRIVILEGES FOR ROLE "pg_database_owner" GRANT ALL PRIVILEGES ON SCHEMAS TO "pg_use_reserved_connections";

-- grant future tables from pg_database_owner to pg_use_reserved_connections in finance_agent (database: finance_agent)
ALTER DEFAULT PRIVILEGES FOR ROLE "pg_database_owner" GRANT ALL PRIVILEGES ON TABLES TO "pg_use_reserved_connections";

-- grant future sequences from pg_database_owner to pg_use_reserved_connections in finance_agent (database: finance_agent)
ALTER DEFAULT PRIVILEGES FOR ROLE "pg_database_owner" GRANT ALL PRIVILEGES ON SEQUENCES TO "pg_use_reserved_connections";

-- grant future functions from pg_database_owner to pg_use_reserved_connections in finance_agent (database: finance_agent)
ALTER DEFAULT PRIVILEGES FOR ROLE "pg_database_owner" GRANT ALL PRIVILEGES ON FUNCTIONS TO "pg_use_reserved_connections";

-- grant future schemas from pg_database_owner to pg_write_all_data in finance_agent (database: finance_agent)
ALTER DEFAULT PRIVILEGES FOR ROLE "pg_database_owner" GRANT ALL PRIVILEGES ON SCHEMAS TO "pg_write_all_data";

-- grant future tables from pg_database_owner to pg_write_all_data in finance_agent (database: finance_agent)
ALTER DEFAULT PRIVILEGES FOR ROLE "pg_database_owner" GRANT ALL PRIVILEGES ON TABLES TO "pg_write_all_data";

-- grant future sequences from pg_database_owner to pg_write_all_data in finance_agent (database: finance_agent)
ALTER DEFAULT PRIVILEGES FOR ROLE "pg_database_owner" GRANT ALL PRIVILEGES ON SEQUENCES TO "pg_write_all_data";

-- grant future functions from pg_database_owner to pg_write_all_data in finance_agent (database: finance_agent)
ALTER DEFAULT PRIVILEGES FOR ROLE "pg_database_owner" GRANT ALL PRIVILEGES ON FUNCTIONS TO "pg_write_all_data";

-- grant future schemas from pg_database_owner to pg_write_server_files in finance_agent (database: finance_agent)
ALTER DEFAULT PRIVILEGES FOR ROLE "pg_database_owner" GRANT ALL PRIVILEGES ON SCHEMAS TO "pg_write_server_files";

-- grant future tables from pg_database_owner to pg_write_server_files in finance_agent (database: finance_agent)
ALTER DEFAULT PRIVILEGES FOR ROLE "pg_database_owner" GRANT ALL PRIVILEGES ON TABLES TO "pg_write_server_files";

-- grant future sequences from pg_database_owner to pg_write_server_files in finance_agent (database: finance_agent)
ALTER DEFAULT PRIVILEGES FOR ROLE "pg_database_owner" GRANT ALL PRIVILEGES ON SEQUENCES TO "pg_write_server_files";

-- grant future functions from pg_database_owner to pg_write_server_files in finance_agent (database: finance_agent)
ALTER DEFAULT PRIVILEGES FOR ROLE "pg_database_owner" GRANT ALL PRIVILEGES ON FUNCTIONS TO "pg_write_server_files";

-- grant future schemas from pg_database_owner to postgres in finance_agent (database: finance_agent)
ALTER DEFAULT PRIVILEGES FOR ROLE "pg_database_owner" GRANT ALL PRIVILEGES ON SCHEMAS TO "postgres";

-- grant future tables from pg_database_owner to postgres in finance_agent (database: finance_agent)
ALTER DEFAULT PRIVILEGES FOR ROLE "pg_database_owner" GRANT ALL PRIVILEGES ON TABLES TO "postgres";

-- grant future sequences from pg_database_owner to postgres in finance_agent (database: finance_agent)
ALTER DEFAULT PRIVILEGES FOR ROLE "pg_database_owner" GRANT ALL PRIVILEGES ON SEQUENCES TO "postgres";

-- grant future functions from pg_database_owner to postgres in finance_agent (database: finance_agent)
ALTER DEFAULT PRIVILEGES FOR ROLE "pg_database_owner" GRANT ALL PRIVILEGES ON FUNCTIONS TO "postgres";

-- grant future schemas from pg_execute_server_program to pg_checkpoint in finance_agent (database: finance_agent)
ALTER DEFAULT PRIVILEGES FOR ROLE "pg_execute_server_program" GRANT ALL PRIVILEGES ON SCHEMAS TO "pg_checkpoint";

-- grant future tables from pg_execute_server_program to pg_checkpoint in finance_agent (database: finance_agent)
ALTER DEFAULT PRIVILEGES FOR ROLE "pg_execute_server_program" GRANT ALL PRIVILEGES ON TABLES TO "pg_checkpoint";

-- grant future sequences from pg_execute_server_program to pg_checkpoint in finance_agent (database: finance_agent)
ALTER DEFAULT PRIVILEGES FOR ROLE "pg_execute_server_program" GRANT ALL PRIVILEGES ON SEQUENCES TO "pg_checkpoint";

-- grant future functions from pg_execute_server_program to pg_checkpoint in finance_agent (database: finance_agent)
ALTER DEFAULT PRIVILEGES FOR ROLE "pg_execute_server_program" GRANT ALL PRIVILEGES ON FUNCTIONS TO "pg_checkpoint";

-- grant future schemas from pg_execute_server_program to pg_create_subscription in finance_agent (database: finance_agent)
ALTER DEFAULT PRIVILEGES FOR ROLE "pg_execute_server_program" GRANT ALL PRIVILEGES ON SCHEMAS TO "pg_create_subscription";

-- grant future tables from pg_execute_server_program to pg_create_subscription in finance_agent (database: finance_agent)
ALTER DEFAULT PRIVILEGES FOR ROLE "pg_execute_server_program" GRANT ALL PRIVILEGES ON TABLES TO "pg_create_subscription";

-- grant future sequences from pg_execute_server_program to pg_create_subscription in finance_agent (database: finance_agent)
ALTER DEFAULT PRIVILEGES FOR ROLE "pg_execute_server_program" GRANT ALL PRIVILEGES ON SEQUENCES TO "pg_create_subscription";

-- grant future functions from pg_execute_server_program to pg_create_subscription in finance_agent (database: finance_agent)
ALTER DEFAULT PRIVILEGES FOR ROLE "pg_execute_server_program" GRANT ALL PRIVILEGES ON FUNCTIONS TO "pg_create_subscription";

-- grant future schemas from pg_execute_server_program to pg_database_owner in finance_agent (database: finance_agent)
ALTER DEFAULT PRIVILEGES FOR ROLE "pg_execute_server_program" GRANT ALL PRIVILEGES ON SCHEMAS TO "pg_database_owner";

-- grant future tables from pg_execute_server_program to pg_database_owner in finance_agent (database: finance_agent)
ALTER DEFAULT PRIVILEGES FOR ROLE "pg_execute_server_program" GRANT ALL PRIVILEGES ON TABLES TO "pg_database_owner";

-- grant future sequences from pg_execute_server_program to pg_database_owner in finance_agent (database: finance_agent)
ALTER DEFAULT PRIVILEGES FOR ROLE "pg_execute_server_program" GRANT ALL PRIVILEGES ON SEQUENCES TO "pg_database_owner";

-- grant future functions from pg_execute_server_program to pg_database_owner in finance_agent (database: finance_agent)
ALTER DEFAULT PRIVILEGES FOR ROLE "pg_execute_server_program" GRANT ALL PRIVILEGES ON FUNCTIONS TO "pg_database_owner";

-- grant future schemas from pg_execute_server_program to pg_maintain in finance_agent (database: finance_agent)
ALTER DEFAULT PRIVILEGES FOR ROLE "pg_execute_server_program" GRANT ALL PRIVILEGES ON SCHEMAS TO "pg_maintain";

-- grant future tables from pg_execute_server_program to pg_maintain in finance_agent (database: finance_agent)
ALTER DEFAULT PRIVILEGES FOR ROLE "pg_execute_server_program" GRANT ALL PRIVILEGES ON TABLES TO "pg_maintain";

-- grant future sequences from pg_execute_server_program to pg_maintain in finance_agent (database: finance_agent)
ALTER DEFAULT PRIVILEGES FOR ROLE "pg_execute_server_program" GRANT ALL PRIVILEGES ON SEQUENCES TO "pg_maintain";

-- grant future functions from pg_execute_server_program to pg_maintain in finance_agent (database: finance_agent)
ALTER DEFAULT PRIVILEGES FOR ROLE "pg_execute_server_program" GRANT ALL PRIVILEGES ON FUNCTIONS TO "pg_maintain";

-- grant future schemas from pg_execute_server_program to pg_monitor in finance_agent (database: finance_agent)
ALTER DEFAULT PRIVILEGES FOR ROLE "pg_execute_server_program" GRANT ALL PRIVILEGES ON SCHEMAS TO "pg_monitor";

-- grant future tables from pg_execute_server_program to pg_monitor in finance_agent (database: finance_agent)
ALTER DEFAULT PRIVILEGES FOR ROLE "pg_execute_server_program" GRANT ALL PRIVILEGES ON TABLES TO "pg_monitor";

-- grant future sequences from pg_execute_server_program to pg_monitor in finance_agent (database: finance_agent)
ALTER DEFAULT PRIVILEGES FOR ROLE "pg_execute_server_program" GRANT ALL PRIVILEGES ON SEQUENCES TO "pg_monitor";

-- grant future functions from pg_execute_server_program to pg_monitor in finance_agent (database: finance_agent)
ALTER DEFAULT PRIVILEGES FOR ROLE "pg_execute_server_program" GRANT ALL PRIVILEGES ON FUNCTIONS TO "pg_monitor";

-- grant future schemas from pg_execute_server_program to pg_read_all_data in finance_agent (database: finance_agent)
ALTER DEFAULT PRIVILEGES FOR ROLE "pg_execute_server_program" GRANT ALL PRIVILEGES ON SCHEMAS TO "pg_read_all_data";

-- grant future tables from pg_execute_server_program to pg_read_all_data in finance_agent (database: finance_agent)
ALTER DEFAULT PRIVILEGES FOR ROLE "pg_execute_server_program" GRANT ALL PRIVILEGES ON TABLES TO "pg_read_all_data";

-- grant future sequences from pg_execute_server_program to pg_read_all_data in finance_agent (database: finance_agent)
ALTER DEFAULT PRIVILEGES FOR ROLE "pg_execute_server_program" GRANT ALL PRIVILEGES ON SEQUENCES TO "pg_read_all_data";

-- grant future functions from pg_execute_server_program to pg_read_all_data in finance_agent (database: finance_agent)
ALTER DEFAULT PRIVILEGES FOR ROLE "pg_execute_server_program" GRANT ALL PRIVILEGES ON FUNCTIONS TO "pg_read_all_data";

-- grant future schemas from pg_execute_server_program to pg_read_all_stats in finance_agent (database: finance_agent)
ALTER DEFAULT PRIVILEGES FOR ROLE "pg_execute_server_program" GRANT ALL PRIVILEGES ON SCHEMAS TO "pg_read_all_stats";

-- grant future tables from pg_execute_server_program to pg_read_all_stats in finance_agent (database: finance_agent)
ALTER DEFAULT PRIVILEGES FOR ROLE "pg_execute_server_program" GRANT ALL PRIVILEGES ON TABLES TO "pg_read_all_stats";

-- grant future sequences from pg_execute_server_program to pg_read_all_stats in finance_agent (database: finance_agent)
ALTER DEFAULT PRIVILEGES FOR ROLE "pg_execute_server_program" GRANT ALL PRIVILEGES ON SEQUENCES TO "pg_read_all_stats";

-- grant future functions from pg_execute_server_program to pg_read_all_stats in finance_agent (database: finance_agent)
ALTER DEFAULT PRIVILEGES FOR ROLE "pg_execute_server_program" GRANT ALL PRIVILEGES ON FUNCTIONS TO "pg_read_all_stats";

-- grant future schemas from pg_execute_server_program to pg_read_all_settings in finance_agent (database: finance_agent)
ALTER DEFAULT PRIVILEGES FOR ROLE "pg_execute_server_program" GRANT ALL PRIVILEGES ON SCHEMAS TO "pg_read_all_settings";

-- grant future tables from pg_execute_server_program to pg_read_all_settings in finance_agent (database: finance_agent)
ALTER DEFAULT PRIVILEGES FOR ROLE "pg_execute_server_program" GRANT ALL PRIVILEGES ON TABLES TO "pg_read_all_settings";

-- grant future sequences from pg_execute_server_program to pg_read_all_settings in finance_agent (database: finance_agent)
ALTER DEFAULT PRIVILEGES FOR ROLE "pg_execute_server_program" GRANT ALL PRIVILEGES ON SEQUENCES TO "pg_read_all_settings";

-- grant future functions from pg_execute_server_program to pg_read_all_settings in finance_agent (database: finance_agent)
ALTER DEFAULT PRIVILEGES FOR ROLE "pg_execute_server_program" GRANT ALL PRIVILEGES ON FUNCTIONS TO "pg_read_all_settings";

-- grant future schemas from pg_execute_server_program to pg_read_server_files in finance_agent (database: finance_agent)
ALTER DEFAULT PRIVILEGES FOR ROLE "pg_execute_server_program" GRANT ALL PRIVILEGES ON SCHEMAS TO "pg_read_server_files";

-- grant future tables from pg_execute_server_program to pg_read_server_files in finance_agent (database: finance_agent)
ALTER DEFAULT PRIVILEGES FOR ROLE "pg_execute_server_program" GRANT ALL PRIVILEGES ON TABLES TO "pg_read_server_files";

-- grant future sequences from pg_execute_server_program to pg_read_server_files in finance_agent (database: finance_agent)
ALTER DEFAULT PRIVILEGES FOR ROLE "pg_execute_server_program" GRANT ALL PRIVILEGES ON SEQUENCES TO "pg_read_server_files";

-- grant future functions from pg_execute_server_program to pg_read_server_files in finance_agent (database: finance_agent)
ALTER DEFAULT PRIVILEGES FOR ROLE "pg_execute_server_program" GRANT ALL PRIVILEGES ON FUNCTIONS TO "pg_read_server_files";

-- grant future schemas from pg_execute_server_program to pg_signal_backend in finance_agent (database: finance_agent)
ALTER DEFAULT PRIVILEGES FOR ROLE "pg_execute_server_program" GRANT ALL PRIVILEGES ON SCHEMAS TO "pg_signal_backend";

-- grant future tables from pg_execute_server_program to pg_signal_backend in finance_agent (database: finance_agent)
ALTER DEFAULT PRIVILEGES FOR ROLE "pg_execute_server_program" GRANT ALL PRIVILEGES ON TABLES TO "pg_signal_backend";

-- grant future sequences from pg_execute_server_program to pg_signal_backend in finance_agent (database: finance_agent)
ALTER DEFAULT PRIVILEGES FOR ROLE "pg_execute_server_program" GRANT ALL PRIVILEGES ON SEQUENCES TO "pg_signal_backend";

-- grant future functions from pg_execute_server_program to pg_signal_backend in finance_agent (database: finance_agent)
ALTER DEFAULT PRIVILEGES FOR ROLE "pg_execute_server_program" GRANT ALL PRIVILEGES ON FUNCTIONS TO "pg_signal_backend";

-- grant future schemas from pg_execute_server_program to pg_stat_scan_tables in finance_agent (database: finance_agent)
ALTER DEFAULT PRIVILEGES FOR ROLE "pg_execute_server_program" GRANT ALL PRIVILEGES ON SCHEMAS TO "pg_stat_scan_tables";

-- grant future tables from pg_execute_server_program to pg_stat_scan_tables in finance_agent (database: finance_agent)
ALTER DEFAULT PRIVILEGES FOR ROLE "pg_execute_server_program" GRANT ALL PRIVILEGES ON TABLES TO "pg_stat_scan_tables";

-- grant future sequences from pg_execute_server_program to pg_stat_scan_tables in finance_agent (database: finance_agent)
ALTER DEFAULT PRIVILEGES FOR ROLE "pg_execute_server_program" GRANT ALL PRIVILEGES ON SEQUENCES TO "pg_stat_scan_tables";

-- grant future functions from pg_execute_server_program to pg_stat_scan_tables in finance_agent (database: finance_agent)
ALTER DEFAULT PRIVILEGES FOR ROLE "pg_execute_server_program" GRANT ALL PRIVILEGES ON FUNCTIONS TO "pg_stat_scan_tables";

-- grant future schemas from pg_execute_server_program to pg_use_reserved_connections in finance_agent (database: finance_agent)
ALTER DEFAULT PRIVILEGES FOR ROLE "pg_execute_server_program" GRANT ALL PRIVILEGES ON SCHEMAS TO "pg_use_reserved_connections";

-- grant future tables from pg_execute_server_program to pg_use_reserved_connections in finance_agent (database: finance_agent)
ALTER DEFAULT PRIVILEGES FOR ROLE "pg_execute_server_program" GRANT ALL PRIVILEGES ON TABLES TO "pg_use_reserved_connections";

-- grant future sequences from pg_execute_server_program to pg_use_reserved_connections in finance_agent (database: finance_agent)
ALTER DEFAULT PRIVILEGES FOR ROLE "pg_execute_server_program" GRANT ALL PRIVILEGES ON SEQUENCES TO "pg_use_reserved_connections";

-- grant future functions from pg_execute_server_program to pg_use_reserved_connections in finance_agent (database: finance_agent)
ALTER DEFAULT PRIVILEGES FOR ROLE "pg_execute_server_program" GRANT ALL PRIVILEGES ON FUNCTIONS TO "pg_use_reserved_connections";

-- grant future schemas from pg_execute_server_program to pg_write_all_data in finance_agent (database: finance_agent)
ALTER DEFAULT PRIVILEGES FOR ROLE "pg_execute_server_program" GRANT ALL PRIVILEGES ON SCHEMAS TO "pg_write_all_data";

-- grant future tables from pg_execute_server_program to pg_write_all_data in finance_agent (database: finance_agent)
ALTER DEFAULT PRIVILEGES FOR ROLE "pg_execute_server_program" GRANT ALL PRIVILEGES ON TABLES TO "pg_write_all_data";

-- grant future sequences from pg_execute_server_program to pg_write_all_data in finance_agent (database: finance_agent)
ALTER DEFAULT PRIVILEGES FOR ROLE "pg_execute_server_program" GRANT ALL PRIVILEGES ON SEQUENCES TO "pg_write_all_data";

-- grant future functions from pg_execute_server_program to pg_write_all_data in finance_agent (database: finance_agent)
ALTER DEFAULT PRIVILEGES FOR ROLE "pg_execute_server_program" GRANT ALL PRIVILEGES ON FUNCTIONS TO "pg_write_all_data";

-- grant future schemas from pg_execute_server_program to pg_write_server_files in finance_agent (database: finance_agent)
ALTER DEFAULT PRIVILEGES FOR ROLE "pg_execute_server_program" GRANT ALL PRIVILEGES ON SCHEMAS TO "pg_write_server_files";

-- grant future tables from pg_execute_server_program to pg_write_server_files in finance_agent (database: finance_agent)
ALTER DEFAULT PRIVILEGES FOR ROLE "pg_execute_server_program" GRANT ALL PRIVILEGES ON TABLES TO "pg_write_server_files";

-- grant future sequences from pg_execute_server_program to pg_write_server_files in finance_agent (database: finance_agent)
ALTER DEFAULT PRIVILEGES FOR ROLE "pg_execute_server_program" GRANT ALL PRIVILEGES ON SEQUENCES TO "pg_write_server_files";

-- grant future functions from pg_execute_server_program to pg_write_server_files in finance_agent (database: finance_agent)
ALTER DEFAULT PRIVILEGES FOR ROLE "pg_execute_server_program" GRANT ALL PRIVILEGES ON FUNCTIONS TO "pg_write_server_files";

-- grant future schemas from pg_execute_server_program to postgres in finance_agent (database: finance_agent)
ALTER DEFAULT PRIVILEGES FOR ROLE "pg_execute_server_program" GRANT ALL PRIVILEGES ON SCHEMAS TO "postgres";

-- grant future tables from pg_execute_server_program to postgres in finance_agent (database: finance_agent)
ALTER DEFAULT PRIVILEGES FOR ROLE "pg_execute_server_program" GRANT ALL PRIVILEGES ON TABLES TO "postgres";

-- grant future sequences from pg_execute_server_program to postgres in finance_agent (database: finance_agent)
ALTER DEFAULT PRIVILEGES FOR ROLE "pg_execute_server_program" GRANT ALL PRIVILEGES ON SEQUENCES TO "postgres";

-- grant future functions from pg_execute_server_program to postgres in finance_agent (database: finance_agent)
ALTER DEFAULT PRIVILEGES FOR ROLE "pg_execute_server_program" GRANT ALL PRIVILEGES ON FUNCTIONS TO "postgres";

-- grant future schemas from pg_maintain to pg_checkpoint in finance_agent (database: finance_agent)
ALTER DEFAULT PRIVILEGES FOR ROLE "pg_maintain" GRANT ALL PRIVILEGES ON SCHEMAS TO "pg_checkpoint";

-- grant future tables from pg_maintain to pg_checkpoint in finance_agent (database: finance_agent)
ALTER DEFAULT PRIVILEGES FOR ROLE "pg_maintain" GRANT ALL PRIVILEGES ON TABLES TO "pg_checkpoint";

-- grant future sequences from pg_maintain to pg_checkpoint in finance_agent (database: finance_agent)
ALTER DEFAULT PRIVILEGES FOR ROLE "pg_maintain" GRANT ALL PRIVILEGES ON SEQUENCES TO "pg_checkpoint";

-- grant future functions from pg_maintain to pg_checkpoint in finance_agent (database: finance_agent)
ALTER DEFAULT PRIVILEGES FOR ROLE "pg_maintain" GRANT ALL PRIVILEGES ON FUNCTIONS TO "pg_checkpoint";

-- grant future schemas from pg_maintain to pg_create_subscription in finance_agent (database: finance_agent)
ALTER DEFAULT PRIVILEGES FOR ROLE "pg_maintain" GRANT ALL PRIVILEGES ON SCHEMAS TO "pg_create_subscription";

-- grant future tables from pg_maintain to pg_create_subscription in finance_agent (database: finance_agent)
ALTER DEFAULT PRIVILEGES FOR ROLE "pg_maintain" GRANT ALL PRIVILEGES ON TABLES TO "pg_create_subscription";

-- grant future sequences from pg_maintain to pg_create_subscription in finance_agent (database: finance_agent)
ALTER DEFAULT PRIVILEGES FOR ROLE "pg_maintain" GRANT ALL PRIVILEGES ON SEQUENCES TO "pg_create_subscription";

-- grant future functions from pg_maintain to pg_create_subscription in finance_agent (database: finance_agent)
ALTER DEFAULT PRIVILEGES FOR ROLE "pg_maintain" GRANT ALL PRIVILEGES ON FUNCTIONS TO "pg_create_subscription";

-- grant future schemas from pg_maintain to pg_database_owner in finance_agent (database: finance_agent)
ALTER DEFAULT PRIVILEGES FOR ROLE "pg_maintain" GRANT ALL PRIVILEGES ON SCHEMAS TO "pg_database_owner";

-- grant future tables from pg_maintain to pg_database_owner in finance_agent (database: finance_agent)
ALTER DEFAULT PRIVILEGES FOR ROLE "pg_maintain" GRANT ALL PRIVILEGES ON TABLES TO "pg_database_owner";

-- grant future sequences from pg_maintain to pg_database_owner in finance_agent (database: finance_agent)
ALTER DEFAULT PRIVILEGES FOR ROLE "pg_maintain" GRANT ALL PRIVILEGES ON SEQUENCES TO "pg_database_owner";

-- grant future functions from pg_maintain to pg_database_owner in finance_agent (database: finance_agent)
ALTER DEFAULT PRIVILEGES FOR ROLE "pg_maintain" GRANT ALL PRIVILEGES ON FUNCTIONS TO "pg_database_owner";

-- grant future schemas from pg_maintain to pg_execute_server_program in finance_agent (database: finance_agent)
ALTER DEFAULT PRIVILEGES FOR ROLE "pg_maintain" GRANT ALL PRIVILEGES ON SCHEMAS TO "pg_execute_server_program";

-- grant future tables from pg_maintain to pg_execute_server_program in finance_agent (database: finance_agent)
ALTER DEFAULT PRIVILEGES FOR ROLE "pg_maintain" GRANT ALL PRIVILEGES ON TABLES TO "pg_execute_server_program";

-- grant future sequences from pg_maintain to pg_execute_server_program in finance_agent (database: finance_agent)
ALTER DEFAULT PRIVILEGES FOR ROLE "pg_maintain" GRANT ALL PRIVILEGES ON SEQUENCES TO "pg_execute_server_program";

-- grant future functions from pg_maintain to pg_execute_server_program in finance_agent (database: finance_agent)
ALTER DEFAULT PRIVILEGES FOR ROLE "pg_maintain" GRANT ALL PRIVILEGES ON FUNCTIONS TO "pg_execute_server_program";

-- grant future schemas from pg_maintain to pg_monitor in finance_agent (database: finance_agent)
ALTER DEFAULT PRIVILEGES FOR ROLE "pg_maintain" GRANT ALL PRIVILEGES ON SCHEMAS TO "pg_monitor";

-- grant future tables from pg_maintain to pg_monitor in finance_agent (database: finance_agent)
ALTER DEFAULT PRIVILEGES FOR ROLE "pg_maintain" GRANT ALL PRIVILEGES ON TABLES TO "pg_monitor";

-- grant future sequences from pg_maintain to pg_monitor in finance_agent (database: finance_agent)
ALTER DEFAULT PRIVILEGES FOR ROLE "pg_maintain" GRANT ALL PRIVILEGES ON SEQUENCES TO "pg_monitor";

-- grant future functions from pg_maintain to pg_monitor in finance_agent (database: finance_agent)
ALTER DEFAULT PRIVILEGES FOR ROLE "pg_maintain" GRANT ALL PRIVILEGES ON FUNCTIONS TO "pg_monitor";

-- grant future schemas from pg_maintain to pg_read_all_data in finance_agent (database: finance_agent)
ALTER DEFAULT PRIVILEGES FOR ROLE "pg_maintain" GRANT ALL PRIVILEGES ON SCHEMAS TO "pg_read_all_data";

-- grant future tables from pg_maintain to pg_read_all_data in finance_agent (database: finance_agent)
ALTER DEFAULT PRIVILEGES FOR ROLE "pg_maintain" GRANT ALL PRIVILEGES ON TABLES TO "pg_read_all_data";

-- grant future sequences from pg_maintain to pg_read_all_data in finance_agent (database: finance_agent)
ALTER DEFAULT PRIVILEGES FOR ROLE "pg_maintain" GRANT ALL PRIVILEGES ON SEQUENCES TO "pg_read_all_data";

-- grant future functions from pg_maintain to pg_read_all_data in finance_agent (database: finance_agent)
ALTER DEFAULT PRIVILEGES FOR ROLE "pg_maintain" GRANT ALL PRIVILEGES ON FUNCTIONS TO "pg_read_all_data";

-- grant future schemas from pg_maintain to pg_read_all_stats in finance_agent (database: finance_agent)
ALTER DEFAULT PRIVILEGES FOR ROLE "pg_maintain" GRANT ALL PRIVILEGES ON SCHEMAS TO "pg_read_all_stats";

-- grant future tables from pg_maintain to pg_read_all_stats in finance_agent (database: finance_agent)
ALTER DEFAULT PRIVILEGES FOR ROLE "pg_maintain" GRANT ALL PRIVILEGES ON TABLES TO "pg_read_all_stats";

-- grant future sequences from pg_maintain to pg_read_all_stats in finance_agent (database: finance_agent)
ALTER DEFAULT PRIVILEGES FOR ROLE "pg_maintain" GRANT ALL PRIVILEGES ON SEQUENCES TO "pg_read_all_stats";

-- grant future functions from pg_maintain to pg_read_all_stats in finance_agent (database: finance_agent)
ALTER DEFAULT PRIVILEGES FOR ROLE "pg_maintain" GRANT ALL PRIVILEGES ON FUNCTIONS TO "pg_read_all_stats";

-- grant future schemas from pg_maintain to pg_read_all_settings in finance_agent (database: finance_agent)
ALTER DEFAULT PRIVILEGES FOR ROLE "pg_maintain" GRANT ALL PRIVILEGES ON SCHEMAS TO "pg_read_all_settings";

-- grant future tables from pg_maintain to pg_read_all_settings in finance_agent (database: finance_agent)
ALTER DEFAULT PRIVILEGES FOR ROLE "pg_maintain" GRANT ALL PRIVILEGES ON TABLES TO "pg_read_all_settings";

-- grant future sequences from pg_maintain to pg_read_all_settings in finance_agent (database: finance_agent)
ALTER DEFAULT PRIVILEGES FOR ROLE "pg_maintain" GRANT ALL PRIVILEGES ON SEQUENCES TO "pg_read_all_settings";

-- grant future functions from pg_maintain to pg_read_all_settings in finance_agent (database: finance_agent)
ALTER DEFAULT PRIVILEGES FOR ROLE "pg_maintain" GRANT ALL PRIVILEGES ON FUNCTIONS TO "pg_read_all_settings";

-- grant future schemas from pg_maintain to pg_read_server_files in finance_agent (database: finance_agent)
ALTER DEFAULT PRIVILEGES FOR ROLE "pg_maintain" GRANT ALL PRIVILEGES ON SCHEMAS TO "pg_read_server_files";

-- grant future tables from pg_maintain to pg_read_server_files in finance_agent (database: finance_agent)
ALTER DEFAULT PRIVILEGES FOR ROLE "pg_maintain" GRANT ALL PRIVILEGES ON TABLES TO "pg_read_server_files";

-- grant future sequences from pg_maintain to pg_read_server_files in finance_agent (database: finance_agent)
ALTER DEFAULT PRIVILEGES FOR ROLE "pg_maintain" GRANT ALL PRIVILEGES ON SEQUENCES TO "pg_read_server_files";

-- grant future functions from pg_maintain to pg_read_server_files in finance_agent (database: finance_agent)
ALTER DEFAULT PRIVILEGES FOR ROLE "pg_maintain" GRANT ALL PRIVILEGES ON FUNCTIONS TO "pg_read_server_files";

-- grant future schemas from pg_maintain to pg_signal_backend in finance_agent (database: finance_agent)
ALTER DEFAULT PRIVILEGES FOR ROLE "pg_maintain" GRANT ALL PRIVILEGES ON SCHEMAS TO "pg_signal_backend";

-- grant future tables from pg_maintain to pg_signal_backend in finance_agent (database: finance_agent)
ALTER DEFAULT PRIVILEGES FOR ROLE "pg_maintain" GRANT ALL PRIVILEGES ON TABLES TO "pg_signal_backend";

-- grant future sequences from pg_maintain to pg_signal_backend in finance_agent (database: finance_agent)
ALTER DEFAULT PRIVILEGES FOR ROLE "pg_maintain" GRANT ALL PRIVILEGES ON SEQUENCES TO "pg_signal_backend";

-- grant future functions from pg_maintain to pg_signal_backend in finance_agent (database: finance_agent)
ALTER DEFAULT PRIVILEGES FOR ROLE "pg_maintain" GRANT ALL PRIVILEGES ON FUNCTIONS TO "pg_signal_backend";

-- grant future schemas from pg_maintain to pg_stat_scan_tables in finance_agent (database: finance_agent)
ALTER DEFAULT PRIVILEGES FOR ROLE "pg_maintain" GRANT ALL PRIVILEGES ON SCHEMAS TO "pg_stat_scan_tables";

-- grant future tables from pg_maintain to pg_stat_scan_tables in finance_agent (database: finance_agent)
ALTER DEFAULT PRIVILEGES FOR ROLE "pg_maintain" GRANT ALL PRIVILEGES ON TABLES TO "pg_stat_scan_tables";

-- grant future sequences from pg_maintain to pg_stat_scan_tables in finance_agent (database: finance_agent)
ALTER DEFAULT PRIVILEGES FOR ROLE "pg_maintain" GRANT ALL PRIVILEGES ON SEQUENCES TO "pg_stat_scan_tables";

-- grant future functions from pg_maintain to pg_stat_scan_tables in finance_agent (database: finance_agent)
ALTER DEFAULT PRIVILEGES FOR ROLE "pg_maintain" GRANT ALL PRIVILEGES ON FUNCTIONS TO "pg_stat_scan_tables";

-- grant future schemas from pg_maintain to pg_use_reserved_connections in finance_agent (database: finance_agent)
ALTER DEFAULT PRIVILEGES FOR ROLE "pg_maintain" GRANT ALL PRIVILEGES ON SCHEMAS TO "pg_use_reserved_connections";

-- grant future tables from pg_maintain to pg_use_reserved_connections in finance_agent (database: finance_agent)
ALTER DEFAULT PRIVILEGES FOR ROLE "pg_maintain" GRANT ALL PRIVILEGES ON TABLES TO "pg_use_reserved_connections";

-- grant future sequences from pg_maintain to pg_use_reserved_connections in finance_agent (database: finance_agent)
ALTER DEFAULT PRIVILEGES FOR ROLE "pg_maintain" GRANT ALL PRIVILEGES ON SEQUENCES TO "pg_use_reserved_connections";

-- grant future functions from pg_maintain to pg_use_reserved_connections in finance_agent (database: finance_agent)
ALTER DEFAULT PRIVILEGES FOR ROLE "pg_maintain" GRANT ALL PRIVILEGES ON FUNCTIONS TO "pg_use_reserved_connections";

-- grant future schemas from pg_maintain to pg_write_all_data in finance_agent (database: finance_agent)
ALTER DEFAULT PRIVILEGES FOR ROLE "pg_maintain" GRANT ALL PRIVILEGES ON SCHEMAS TO "pg_write_all_data";

-- grant future tables from pg_maintain to pg_write_all_data in finance_agent (database: finance_agent)
ALTER DEFAULT PRIVILEGES FOR ROLE "pg_maintain" GRANT ALL PRIVILEGES ON TABLES TO "pg_write_all_data";

-- grant future sequences from pg_maintain to pg_write_all_data in finance_agent (database: finance_agent)
ALTER DEFAULT PRIVILEGES FOR ROLE "pg_maintain" GRANT ALL PRIVILEGES ON SEQUENCES TO "pg_write_all_data";

-- grant future functions from pg_maintain to pg_write_all_data in finance_agent (database: finance_agent)
ALTER DEFAULT PRIVILEGES FOR ROLE "pg_maintain" GRANT ALL PRIVILEGES ON FUNCTIONS TO "pg_write_all_data";

-- grant future schemas from pg_maintain to pg_write_server_files in finance_agent (database: finance_agent)
ALTER DEFAULT PRIVILEGES FOR ROLE "pg_maintain" GRANT ALL PRIVILEGES ON SCHEMAS TO "pg_write_server_files";

-- grant future tables from pg_maintain to pg_write_server_files in finance_agent (database: finance_agent)
ALTER DEFAULT PRIVILEGES FOR ROLE "pg_maintain" GRANT ALL PRIVILEGES ON TABLES TO "pg_write_server_files";

-- grant future sequences from pg_maintain to pg_write_server_files in finance_agent (database: finance_agent)
ALTER DEFAULT PRIVILEGES FOR ROLE "pg_maintain" GRANT ALL PRIVILEGES ON SEQUENCES TO "pg_write_server_files";

-- grant future functions from pg_maintain to pg_write_server_files in finance_agent (database: finance_agent)
ALTER DEFAULT PRIVILEGES FOR ROLE "pg_maintain" GRANT ALL PRIVILEGES ON FUNCTIONS TO "pg_write_server_files";

-- grant future schemas from pg_maintain to postgres in finance_agent (database: finance_agent)
ALTER DEFAULT PRIVILEGES FOR ROLE "pg_maintain" GRANT ALL PRIVILEGES ON SCHEMAS TO "postgres";

-- grant future tables from pg_maintain to postgres in finance_agent (database: finance_agent)
ALTER DEFAULT PRIVILEGES FOR ROLE "pg_maintain" GRANT ALL PRIVILEGES ON TABLES TO "postgres";

-- grant future sequences from pg_maintain to postgres in finance_agent (database: finance_agent)
ALTER DEFAULT PRIVILEGES FOR ROLE "pg_maintain" GRANT ALL PRIVILEGES ON SEQUENCES TO "postgres";

-- grant future functions from pg_maintain to postgres in finance_agent (database: finance_agent)
ALTER DEFAULT PRIVILEGES FOR ROLE "pg_maintain" GRANT ALL PRIVILEGES ON FUNCTIONS TO "postgres";

-- grant future schemas from pg_monitor to pg_checkpoint in finance_agent (database: finance_agent)
ALTER DEFAULT PRIVILEGES FOR ROLE "pg_monitor" GRANT ALL PRIVILEGES ON SCHEMAS TO "pg_checkpoint";

-- grant future tables from pg_monitor to pg_checkpoint in finance_agent (database: finance_agent)
ALTER DEFAULT PRIVILEGES FOR ROLE "pg_monitor" GRANT ALL PRIVILEGES ON TABLES TO "pg_checkpoint";

-- grant future sequences from pg_monitor to pg_checkpoint in finance_agent (database: finance_agent)
ALTER DEFAULT PRIVILEGES FOR ROLE "pg_monitor" GRANT ALL PRIVILEGES ON SEQUENCES TO "pg_checkpoint";

-- grant future functions from pg_monitor to pg_checkpoint in finance_agent (database: finance_agent)
ALTER DEFAULT PRIVILEGES FOR ROLE "pg_monitor" GRANT ALL PRIVILEGES ON FUNCTIONS TO "pg_checkpoint";

-- grant future schemas from pg_monitor to pg_create_subscription in finance_agent (database: finance_agent)
ALTER DEFAULT PRIVILEGES FOR ROLE "pg_monitor" GRANT ALL PRIVILEGES ON SCHEMAS TO "pg_create_subscription";

-- grant future tables from pg_monitor to pg_create_subscription in finance_agent (database: finance_agent)
ALTER DEFAULT PRIVILEGES FOR ROLE "pg_monitor" GRANT ALL PRIVILEGES ON TABLES TO "pg_create_subscription";

-- grant future sequences from pg_monitor to pg_create_subscription in finance_agent (database: finance_agent)
ALTER DEFAULT PRIVILEGES FOR ROLE "pg_monitor" GRANT ALL PRIVILEGES ON SEQUENCES TO "pg_create_subscription";

-- grant future functions from pg_monitor to pg_create_subscription in finance_agent (database: finance_agent)
ALTER DEFAULT PRIVILEGES FOR ROLE "pg_monitor" GRANT ALL PRIVILEGES ON FUNCTIONS TO "pg_create_subscription";

-- grant future schemas from pg_monitor to pg_database_owner in finance_agent (database: finance_agent)
ALTER DEFAULT PRIVILEGES FOR ROLE "pg_monitor" GRANT ALL PRIVILEGES ON SCHEMAS TO "pg_database_owner";

-- grant future tables from pg_monitor to pg_database_owner in finance_agent (database: finance_agent)
ALTER DEFAULT PRIVILEGES FOR ROLE "pg_monitor" GRANT ALL PRIVILEGES ON TABLES TO "pg_database_owner";

-- grant future sequences from pg_monitor to pg_database_owner in finance_agent (database: finance_agent)
ALTER DEFAULT PRIVILEGES FOR ROLE "pg_monitor" GRANT ALL PRIVILEGES ON SEQUENCES TO "pg_database_owner";

-- grant future functions from pg_monitor to pg_database_owner in finance_agent (database: finance_agent)
ALTER DEFAULT PRIVILEGES FOR ROLE "pg_monitor" GRANT ALL PRIVILEGES ON FUNCTIONS TO "pg_database_owner";

-- grant future schemas from pg_monitor to pg_execute_server_program in finance_agent (database: finance_agent)
ALTER DEFAULT PRIVILEGES FOR ROLE "pg_monitor" GRANT ALL PRIVILEGES ON SCHEMAS TO "pg_execute_server_program";

-- grant future tables from pg_monitor to pg_execute_server_program in finance_agent (database: finance_agent)
ALTER DEFAULT PRIVILEGES FOR ROLE "pg_monitor" GRANT ALL PRIVILEGES ON TABLES TO "pg_execute_server_program";

-- grant future sequences from pg_monitor to pg_execute_server_program in finance_agent (database: finance_agent)
ALTER DEFAULT PRIVILEGES FOR ROLE "pg_monitor" GRANT ALL PRIVILEGES ON SEQUENCES TO "pg_execute_server_program";

-- grant future functions from pg_monitor to pg_execute_server_program in finance_agent (database: finance_agent)
ALTER DEFAULT PRIVILEGES FOR ROLE "pg_monitor" GRANT ALL PRIVILEGES ON FUNCTIONS TO "pg_execute_server_program";

-- grant future schemas from pg_monitor to pg_maintain in finance_agent (database: finance_agent)
ALTER DEFAULT PRIVILEGES FOR ROLE "pg_monitor" GRANT ALL PRIVILEGES ON SCHEMAS TO "pg_maintain";

-- grant future tables from pg_monitor to pg_maintain in finance_agent (database: finance_agent)
ALTER DEFAULT PRIVILEGES FOR ROLE "pg_monitor" GRANT ALL PRIVILEGES ON TABLES TO "pg_maintain";

-- grant future sequences from pg_monitor to pg_maintain in finance_agent (database: finance_agent)
ALTER DEFAULT PRIVILEGES FOR ROLE "pg_monitor" GRANT ALL PRIVILEGES ON SEQUENCES TO "pg_maintain";

-- grant future functions from pg_monitor to pg_maintain in finance_agent (database: finance_agent)
ALTER DEFAULT PRIVILEGES FOR ROLE "pg_monitor" GRANT ALL PRIVILEGES ON FUNCTIONS TO "pg_maintain";

-- grant future schemas from pg_monitor to pg_read_all_data in finance_agent (database: finance_agent)
ALTER DEFAULT PRIVILEGES FOR ROLE "pg_monitor" GRANT ALL PRIVILEGES ON SCHEMAS TO "pg_read_all_data";

-- grant future tables from pg_monitor to pg_read_all_data in finance_agent (database: finance_agent)
ALTER DEFAULT PRIVILEGES FOR ROLE "pg_monitor" GRANT ALL PRIVILEGES ON TABLES TO "pg_read_all_data";

-- grant future sequences from pg_monitor to pg_read_all_data in finance_agent (database: finance_agent)
ALTER DEFAULT PRIVILEGES FOR ROLE "pg_monitor" GRANT ALL PRIVILEGES ON SEQUENCES TO "pg_read_all_data";

-- grant future functions from pg_monitor to pg_read_all_data in finance_agent (database: finance_agent)
ALTER DEFAULT PRIVILEGES FOR ROLE "pg_monitor" GRANT ALL PRIVILEGES ON FUNCTIONS TO "pg_read_all_data";

-- grant future schemas from pg_monitor to pg_read_all_stats in finance_agent (database: finance_agent)
ALTER DEFAULT PRIVILEGES FOR ROLE "pg_monitor" GRANT ALL PRIVILEGES ON SCHEMAS TO "pg_read_all_stats";

-- grant future tables from pg_monitor to pg_read_all_stats in finance_agent (database: finance_agent)
ALTER DEFAULT PRIVILEGES FOR ROLE "pg_monitor" GRANT ALL PRIVILEGES ON TABLES TO "pg_read_all_stats";

-- grant future sequences from pg_monitor to pg_read_all_stats in finance_agent (database: finance_agent)
ALTER DEFAULT PRIVILEGES FOR ROLE "pg_monitor" GRANT ALL PRIVILEGES ON SEQUENCES TO "pg_read_all_stats";

-- grant future functions from pg_monitor to pg_read_all_stats in finance_agent (database: finance_agent)
ALTER DEFAULT PRIVILEGES FOR ROLE "pg_monitor" GRANT ALL PRIVILEGES ON FUNCTIONS TO "pg_read_all_stats";

-- grant future schemas from pg_monitor to pg_read_all_settings in finance_agent (database: finance_agent)
ALTER DEFAULT PRIVILEGES FOR ROLE "pg_monitor" GRANT ALL PRIVILEGES ON SCHEMAS TO "pg_read_all_settings";

-- grant future tables from pg_monitor to pg_read_all_settings in finance_agent (database: finance_agent)
ALTER DEFAULT PRIVILEGES FOR ROLE "pg_monitor" GRANT ALL PRIVILEGES ON TABLES TO "pg_read_all_settings";

-- grant future sequences from pg_monitor to pg_read_all_settings in finance_agent (database: finance_agent)
ALTER DEFAULT PRIVILEGES FOR ROLE "pg_monitor" GRANT ALL PRIVILEGES ON SEQUENCES TO "pg_read_all_settings";

-- grant future functions from pg_monitor to pg_read_all_settings in finance_agent (database: finance_agent)
ALTER DEFAULT PRIVILEGES FOR ROLE "pg_monitor" GRANT ALL PRIVILEGES ON FUNCTIONS TO "pg_read_all_settings";

-- grant future schemas from pg_monitor to pg_read_server_files in finance_agent (database: finance_agent)
ALTER DEFAULT PRIVILEGES FOR ROLE "pg_monitor" GRANT ALL PRIVILEGES ON SCHEMAS TO "pg_read_server_files";

-- grant future tables from pg_monitor to pg_read_server_files in finance_agent (database: finance_agent)
ALTER DEFAULT PRIVILEGES FOR ROLE "pg_monitor" GRANT ALL PRIVILEGES ON TABLES TO "pg_read_server_files";

-- grant future sequences from pg_monitor to pg_read_server_files in finance_agent (database: finance_agent)
ALTER DEFAULT PRIVILEGES FOR ROLE "pg_monitor" GRANT ALL PRIVILEGES ON SEQUENCES TO "pg_read_server_files";

-- grant future functions from pg_monitor to pg_read_server_files in finance_agent (database: finance_agent)
ALTER DEFAULT PRIVILEGES FOR ROLE "pg_monitor" GRANT ALL PRIVILEGES ON FUNCTIONS TO "pg_read_server_files";

-- grant future schemas from pg_monitor to pg_signal_backend in finance_agent (database: finance_agent)
ALTER DEFAULT PRIVILEGES FOR ROLE "pg_monitor" GRANT ALL PRIVILEGES ON SCHEMAS TO "pg_signal_backend";

-- grant future tables from pg_monitor to pg_signal_backend in finance_agent (database: finance_agent)
ALTER DEFAULT PRIVILEGES FOR ROLE "pg_monitor" GRANT ALL PRIVILEGES ON TABLES TO "pg_signal_backend";

-- grant future sequences from pg_monitor to pg_signal_backend in finance_agent (database: finance_agent)
ALTER DEFAULT PRIVILEGES FOR ROLE "pg_monitor" GRANT ALL PRIVILEGES ON SEQUENCES TO "pg_signal_backend";

-- grant future functions from pg_monitor to pg_signal_backend in finance_agent (database: finance_agent)
ALTER DEFAULT PRIVILEGES FOR ROLE "pg_monitor" GRANT ALL PRIVILEGES ON FUNCTIONS TO "pg_signal_backend";

-- grant future schemas from pg_monitor to pg_stat_scan_tables in finance_agent (database: finance_agent)
ALTER DEFAULT PRIVILEGES FOR ROLE "pg_monitor" GRANT ALL PRIVILEGES ON SCHEMAS TO "pg_stat_scan_tables";

-- grant future tables from pg_monitor to pg_stat_scan_tables in finance_agent (database: finance_agent)
ALTER DEFAULT PRIVILEGES FOR ROLE "pg_monitor" GRANT ALL PRIVILEGES ON TABLES TO "pg_stat_scan_tables";

-- grant future sequences from pg_monitor to pg_stat_scan_tables in finance_agent (database: finance_agent)
ALTER DEFAULT PRIVILEGES FOR ROLE "pg_monitor" GRANT ALL PRIVILEGES ON SEQUENCES TO "pg_stat_scan_tables";

-- grant future functions from pg_monitor to pg_stat_scan_tables in finance_agent (database: finance_agent)
ALTER DEFAULT PRIVILEGES FOR ROLE "pg_monitor" GRANT ALL PRIVILEGES ON FUNCTIONS TO "pg_stat_scan_tables";

-- grant future schemas from pg_monitor to pg_use_reserved_connections in finance_agent (database: finance_agent)
ALTER DEFAULT PRIVILEGES FOR ROLE "pg_monitor" GRANT ALL PRIVILEGES ON SCHEMAS TO "pg_use_reserved_connections";

-- grant future tables from pg_monitor to pg_use_reserved_connections in finance_agent (database: finance_agent)
ALTER DEFAULT PRIVILEGES FOR ROLE "pg_monitor" GRANT ALL PRIVILEGES ON TABLES TO "pg_use_reserved_connections";

-- grant future sequences from pg_monitor to pg_use_reserved_connections in finance_agent (database: finance_agent)
ALTER DEFAULT PRIVILEGES FOR ROLE "pg_monitor" GRANT ALL PRIVILEGES ON SEQUENCES TO "pg_use_reserved_connections";

-- grant future functions from pg_monitor to pg_use_reserved_connections in finance_agent (database: finance_agent)
ALTER DEFAULT PRIVILEGES FOR ROLE "pg_monitor" GRANT ALL PRIVILEGES ON FUNCTIONS TO "pg_use_reserved_connections";

-- grant future schemas from pg_monitor to pg_write_all_data in finance_agent (database: finance_agent)
ALTER DEFAULT PRIVILEGES FOR ROLE "pg_monitor" GRANT ALL PRIVILEGES ON SCHEMAS TO "pg_write_all_data";

-- grant future tables from pg_monitor to pg_write_all_data in finance_agent (database: finance_agent)
ALTER DEFAULT PRIVILEGES FOR ROLE "pg_monitor" GRANT ALL PRIVILEGES ON TABLES TO "pg_write_all_data";

-- grant future sequences from pg_monitor to pg_write_all_data in finance_agent (database: finance_agent)
ALTER DEFAULT PRIVILEGES FOR ROLE "pg_monitor" GRANT ALL PRIVILEGES ON SEQUENCES TO "pg_write_all_data";

-- grant future functions from pg_monitor to pg_write_all_data in finance_agent (database: finance_agent)
ALTER DEFAULT PRIVILEGES FOR ROLE "pg_monitor" GRANT ALL PRIVILEGES ON FUNCTIONS TO "pg_write_all_data";

-- grant future schemas from pg_monitor to pg_write_server_files in finance_agent (database: finance_agent)
ALTER DEFAULT PRIVILEGES FOR ROLE "pg_monitor" GRANT ALL PRIVILEGES ON SCHEMAS TO "pg_write_server_files";

-- grant future tables from pg_monitor to pg_write_server_files in finance_agent (database: finance_agent)
ALTER DEFAULT PRIVILEGES FOR ROLE "pg_monitor" GRANT ALL PRIVILEGES ON TABLES TO "pg_write_server_files";

-- grant future sequences from pg_monitor to pg_write_server_files in finance_agent (database: finance_agent)
ALTER DEFAULT PRIVILEGES FOR ROLE "pg_monitor" GRANT ALL PRIVILEGES ON SEQUENCES TO "pg_write_server_files";

-- grant future functions from pg_monitor to pg_write_server_files in finance_agent (database: finance_agent)
ALTER DEFAULT PRIVILEGES FOR ROLE "pg_monitor" GRANT ALL PRIVILEGES ON FUNCTIONS TO "pg_write_server_files";

-- grant future schemas from pg_monitor to postgres in finance_agent (database: finance_agent)
ALTER DEFAULT PRIVILEGES FOR ROLE "pg_monitor" GRANT ALL PRIVILEGES ON SCHEMAS TO "postgres";

-- grant future tables from pg_monitor to postgres in finance_agent (database: finance_agent)
ALTER DEFAULT PRIVILEGES FOR ROLE "pg_monitor" GRANT ALL PRIVILEGES ON TABLES TO "postgres";

-- grant future sequences from pg_monitor to postgres in finance_agent (database: finance_agent)
ALTER DEFAULT PRIVILEGES FOR ROLE "pg_monitor" GRANT ALL PRIVILEGES ON SEQUENCES TO "postgres";

-- grant future functions from pg_monitor to postgres in finance_agent (database: finance_agent)
ALTER DEFAULT PRIVILEGES FOR ROLE "pg_monitor" GRANT ALL PRIVILEGES ON FUNCTIONS TO "postgres";

-- grant future schemas from pg_read_all_data to pg_checkpoint in finance_agent (database: finance_agent)
ALTER DEFAULT PRIVILEGES FOR ROLE "pg_read_all_data" GRANT ALL PRIVILEGES ON SCHEMAS TO "pg_checkpoint";

-- grant future tables from pg_read_all_data to pg_checkpoint in finance_agent (database: finance_agent)
ALTER DEFAULT PRIVILEGES FOR ROLE "pg_read_all_data" GRANT ALL PRIVILEGES ON TABLES TO "pg_checkpoint";

-- grant future sequences from pg_read_all_data to pg_checkpoint in finance_agent (database: finance_agent)
ALTER DEFAULT PRIVILEGES FOR ROLE "pg_read_all_data" GRANT ALL PRIVILEGES ON SEQUENCES TO "pg_checkpoint";

-- grant future functions from pg_read_all_data to pg_checkpoint in finance_agent (database: finance_agent)
ALTER DEFAULT PRIVILEGES FOR ROLE "pg_read_all_data" GRANT ALL PRIVILEGES ON FUNCTIONS TO "pg_checkpoint";

-- grant future schemas from pg_read_all_data to pg_create_subscription in finance_agent (database: finance_agent)
ALTER DEFAULT PRIVILEGES FOR ROLE "pg_read_all_data" GRANT ALL PRIVILEGES ON SCHEMAS TO "pg_create_subscription";

-- grant future tables from pg_read_all_data to pg_create_subscription in finance_agent (database: finance_agent)
ALTER DEFAULT PRIVILEGES FOR ROLE "pg_read_all_data" GRANT ALL PRIVILEGES ON TABLES TO "pg_create_subscription";

-- grant future sequences from pg_read_all_data to pg_create_subscription in finance_agent (database: finance_agent)
ALTER DEFAULT PRIVILEGES FOR ROLE "pg_read_all_data" GRANT ALL PRIVILEGES ON SEQUENCES TO "pg_create_subscription";

-- grant future functions from pg_read_all_data to pg_create_subscription in finance_agent (database: finance_agent)
ALTER DEFAULT PRIVILEGES FOR ROLE "pg_read_all_data" GRANT ALL PRIVILEGES ON FUNCTIONS TO "pg_create_subscription";

-- grant future schemas from pg_read_all_data to pg_database_owner in finance_agent (database: finance_agent)
ALTER DEFAULT PRIVILEGES FOR ROLE "pg_read_all_data" GRANT ALL PRIVILEGES ON SCHEMAS TO "pg_database_owner";

-- grant future tables from pg_read_all_data to pg_database_owner in finance_agent (database: finance_agent)
ALTER DEFAULT PRIVILEGES FOR ROLE "pg_read_all_data" GRANT ALL PRIVILEGES ON TABLES TO "pg_database_owner";

-- grant future sequences from pg_read_all_data to pg_database_owner in finance_agent (database: finance_agent)
ALTER DEFAULT PRIVILEGES FOR ROLE "pg_read_all_data" GRANT ALL PRIVILEGES ON SEQUENCES TO "pg_database_owner";

-- grant future functions from pg_read_all_data to pg_database_owner in finance_agent (database: finance_agent)
ALTER DEFAULT PRIVILEGES FOR ROLE "pg_read_all_data" GRANT ALL PRIVILEGES ON FUNCTIONS TO "pg_database_owner";

-- grant future schemas from pg_read_all_data to pg_execute_server_program in finance_agent (database: finance_agent)
ALTER DEFAULT PRIVILEGES FOR ROLE "pg_read_all_data" GRANT ALL PRIVILEGES ON SCHEMAS TO "pg_execute_server_program";

-- grant future tables from pg_read_all_data to pg_execute_server_program in finance_agent (database: finance_agent)
ALTER DEFAULT PRIVILEGES FOR ROLE "pg_read_all_data" GRANT ALL PRIVILEGES ON TABLES TO "pg_execute_server_program";

-- grant future sequences from pg_read_all_data to pg_execute_server_program in finance_agent (database: finance_agent)
ALTER DEFAULT PRIVILEGES FOR ROLE "pg_read_all_data" GRANT ALL PRIVILEGES ON SEQUENCES TO "pg_execute_server_program";

-- grant future functions from pg_read_all_data to pg_execute_server_program in finance_agent (database: finance_agent)
ALTER DEFAULT PRIVILEGES FOR ROLE "pg_read_all_data" GRANT ALL PRIVILEGES ON FUNCTIONS TO "pg_execute_server_program";

-- grant future schemas from pg_read_all_data to pg_maintain in finance_agent (database: finance_agent)
ALTER DEFAULT PRIVILEGES FOR ROLE "pg_read_all_data" GRANT ALL PRIVILEGES ON SCHEMAS TO "pg_maintain";

-- grant future tables from pg_read_all_data to pg_maintain in finance_agent (database: finance_agent)
ALTER DEFAULT PRIVILEGES FOR ROLE "pg_read_all_data" GRANT ALL PRIVILEGES ON TABLES TO "pg_maintain";

-- grant future sequences from pg_read_all_data to pg_maintain in finance_agent (database: finance_agent)
ALTER DEFAULT PRIVILEGES FOR ROLE "pg_read_all_data" GRANT ALL PRIVILEGES ON SEQUENCES TO "pg_maintain";

-- grant future functions from pg_read_all_data to pg_maintain in finance_agent (database: finance_agent)
ALTER DEFAULT PRIVILEGES FOR ROLE "pg_read_all_data" GRANT ALL PRIVILEGES ON FUNCTIONS TO "pg_maintain";

-- grant future schemas from pg_read_all_data to pg_monitor in finance_agent (database: finance_agent)
ALTER DEFAULT PRIVILEGES FOR ROLE "pg_read_all_data" GRANT ALL PRIVILEGES ON SCHEMAS TO "pg_monitor";

-- grant future tables from pg_read_all_data to pg_monitor in finance_agent (database: finance_agent)
ALTER DEFAULT PRIVILEGES FOR ROLE "pg_read_all_data" GRANT ALL PRIVILEGES ON TABLES TO "pg_monitor";

-- grant future sequences from pg_read_all_data to pg_monitor in finance_agent (database: finance_agent)
ALTER DEFAULT PRIVILEGES FOR ROLE "pg_read_all_data" GRANT ALL PRIVILEGES ON SEQUENCES TO "pg_monitor";

-- grant future functions from pg_read_all_data to pg_monitor in finance_agent (database: finance_agent)
ALTER DEFAULT PRIVILEGES FOR ROLE "pg_read_all_data" GRANT ALL PRIVILEGES ON FUNCTIONS TO "pg_monitor";

-- grant future schemas from pg_read_all_data to pg_read_all_stats in finance_agent (database: finance_agent)
ALTER DEFAULT PRIVILEGES FOR ROLE "pg_read_all_data" GRANT ALL PRIVILEGES ON SCHEMAS TO "pg_read_all_stats";

-- grant future tables from pg_read_all_data to pg_read_all_stats in finance_agent (database: finance_agent)
ALTER DEFAULT PRIVILEGES FOR ROLE "pg_read_all_data" GRANT ALL PRIVILEGES ON TABLES TO "pg_read_all_stats";

-- grant future sequences from pg_read_all_data to pg_read_all_stats in finance_agent (database: finance_agent)
ALTER DEFAULT PRIVILEGES FOR ROLE "pg_read_all_data" GRANT ALL PRIVILEGES ON SEQUENCES TO "pg_read_all_stats";

-- grant future functions from pg_read_all_data to pg_read_all_stats in finance_agent (database: finance_agent)
ALTER DEFAULT PRIVILEGES FOR ROLE "pg_read_all_data" GRANT ALL PRIVILEGES ON FUNCTIONS TO "pg_read_all_stats";

-- grant future schemas from pg_read_all_data to pg_read_all_settings in finance_agent (database: finance_agent)
ALTER DEFAULT PRIVILEGES FOR ROLE "pg_read_all_data" GRANT ALL PRIVILEGES ON SCHEMAS TO "pg_read_all_settings";

-- grant future tables from pg_read_all_data to pg_read_all_settings in finance_agent (database: finance_agent)
ALTER DEFAULT PRIVILEGES FOR ROLE "pg_read_all_data" GRANT ALL PRIVILEGES ON TABLES TO "pg_read_all_settings";

-- grant future sequences from pg_read_all_data to pg_read_all_settings in finance_agent (database: finance_agent)
ALTER DEFAULT PRIVILEGES FOR ROLE "pg_read_all_data" GRANT ALL PRIVILEGES ON SEQUENCES TO "pg_read_all_settings";

-- grant future functions from pg_read_all_data to pg_read_all_settings in finance_agent (database: finance_agent)
ALTER DEFAULT PRIVILEGES FOR ROLE "pg_read_all_data" GRANT ALL PRIVILEGES ON FUNCTIONS TO "pg_read_all_settings";

-- grant future schemas from pg_read_all_data to pg_read_server_files in finance_agent (database: finance_agent)
ALTER DEFAULT PRIVILEGES FOR ROLE "pg_read_all_data" GRANT ALL PRIVILEGES ON SCHEMAS TO "pg_read_server_files";

-- grant future tables from pg_read_all_data to pg_read_server_files in finance_agent (database: finance_agent)
ALTER DEFAULT PRIVILEGES FOR ROLE "pg_read_all_data" GRANT ALL PRIVILEGES ON TABLES TO "pg_read_server_files";

-- grant future sequences from pg_read_all_data to pg_read_server_files in finance_agent (database: finance_agent)
ALTER DEFAULT PRIVILEGES FOR ROLE "pg_read_all_data" GRANT ALL PRIVILEGES ON SEQUENCES TO "pg_read_server_files";

-- grant future functions from pg_read_all_data to pg_read_server_files in finance_agent (database: finance_agent)
ALTER DEFAULT PRIVILEGES FOR ROLE "pg_read_all_data" GRANT ALL PRIVILEGES ON FUNCTIONS TO "pg_read_server_files";

-- grant future schemas from pg_read_all_data to pg_signal_backend in finance_agent (database: finance_agent)
ALTER DEFAULT PRIVILEGES FOR ROLE "pg_read_all_data" GRANT ALL PRIVILEGES ON SCHEMAS TO "pg_signal_backend";

-- grant future tables from pg_read_all_data to pg_signal_backend in finance_agent (database: finance_agent)
ALTER DEFAULT PRIVILEGES FOR ROLE "pg_read_all_data" GRANT ALL PRIVILEGES ON TABLES TO "pg_signal_backend";

-- grant future sequences from pg_read_all_data to pg_signal_backend in finance_agent (database: finance_agent)
ALTER DEFAULT PRIVILEGES FOR ROLE "pg_read_all_data" GRANT ALL PRIVILEGES ON SEQUENCES TO "pg_signal_backend";

-- grant future functions from pg_read_all_data to pg_signal_backend in finance_agent (database: finance_agent)
ALTER DEFAULT PRIVILEGES FOR ROLE "pg_read_all_data" GRANT ALL PRIVILEGES ON FUNCTIONS TO "pg_signal_backend";

-- grant future schemas from pg_read_all_data to pg_stat_scan_tables in finance_agent (database: finance_agent)
ALTER DEFAULT PRIVILEGES FOR ROLE "pg_read_all_data" GRANT ALL PRIVILEGES ON SCHEMAS TO "pg_stat_scan_tables";

-- grant future tables from pg_read_all_data to pg_stat_scan_tables in finance_agent (database: finance_agent)
ALTER DEFAULT PRIVILEGES FOR ROLE "pg_read_all_data" GRANT ALL PRIVILEGES ON TABLES TO "pg_stat_scan_tables";

-- grant future sequences from pg_read_all_data to pg_stat_scan_tables in finance_agent (database: finance_agent)
ALTER DEFAULT PRIVILEGES FOR ROLE "pg_read_all_data" GRANT ALL PRIVILEGES ON SEQUENCES TO "pg_stat_scan_tables";

-- grant future functions from pg_read_all_data to pg_stat_scan_tables in finance_agent (database: finance_agent)
ALTER DEFAULT PRIVILEGES FOR ROLE "pg_read_all_data" GRANT ALL PRIVILEGES ON FUNCTIONS TO "pg_stat_scan_tables";

-- grant future schemas from pg_read_all_data to pg_use_reserved_connections in finance_agent (database: finance_agent)
ALTER DEFAULT PRIVILEGES FOR ROLE "pg_read_all_data" GRANT ALL PRIVILEGES ON SCHEMAS TO "pg_use_reserved_connections";

-- grant future tables from pg_read_all_data to pg_use_reserved_connections in finance_agent (database: finance_agent)
ALTER DEFAULT PRIVILEGES FOR ROLE "pg_read_all_data" GRANT ALL PRIVILEGES ON TABLES TO "pg_use_reserved_connections";

-- grant future sequences from pg_read_all_data to pg_use_reserved_connections in finance_agent (database: finance_agent)
ALTER DEFAULT PRIVILEGES FOR ROLE "pg_read_all_data" GRANT ALL PRIVILEGES ON SEQUENCES TO "pg_use_reserved_connections";

-- grant future functions from pg_read_all_data to pg_use_reserved_connections in finance_agent (database: finance_agent)
ALTER DEFAULT PRIVILEGES FOR ROLE "pg_read_all_data" GRANT ALL PRIVILEGES ON FUNCTIONS TO "pg_use_reserved_connections";

-- grant future schemas from pg_read_all_data to pg_write_all_data in finance_agent (database: finance_agent)
ALTER DEFAULT PRIVILEGES FOR ROLE "pg_read_all_data" GRANT ALL PRIVILEGES ON SCHEMAS TO "pg_write_all_data";

-- grant future tables from pg_read_all_data to pg_write_all_data in finance_agent (database: finance_agent)
ALTER DEFAULT PRIVILEGES FOR ROLE "pg_read_all_data" GRANT ALL PRIVILEGES ON TABLES TO "pg_write_all_data";

-- grant future sequences from pg_read_all_data to pg_write_all_data in finance_agent (database: finance_agent)
ALTER DEFAULT PRIVILEGES FOR ROLE "pg_read_all_data" GRANT ALL PRIVILEGES ON SEQUENCES TO "pg_write_all_data";

-- grant future functions from pg_read_all_data to pg_write_all_data in finance_agent (database: finance_agent)
ALTER DEFAULT PRIVILEGES FOR ROLE "pg_read_all_data" GRANT ALL PRIVILEGES ON FUNCTIONS TO "pg_write_all_data";

-- grant future schemas from pg_read_all_data to pg_write_server_files in finance_agent (database: finance_agent)
ALTER DEFAULT PRIVILEGES FOR ROLE "pg_read_all_data" GRANT ALL PRIVILEGES ON SCHEMAS TO "pg_write_server_files";

-- grant future tables from pg_read_all_data to pg_write_server_files in finance_agent (database: finance_agent)
ALTER DEFAULT PRIVILEGES FOR ROLE "pg_read_all_data" GRANT ALL PRIVILEGES ON TABLES TO "pg_write_server_files";

-- grant future sequences from pg_read_all_data to pg_write_server_files in finance_agent (database: finance_agent)
ALTER DEFAULT PRIVILEGES FOR ROLE "pg_read_all_data" GRANT ALL PRIVILEGES ON SEQUENCES TO "pg_write_server_files";

-- grant future functions from pg_read_all_data to pg_write_server_files in finance_agent (database: finance_agent)
ALTER DEFAULT PRIVILEGES FOR ROLE "pg_read_all_data" GRANT ALL PRIVILEGES ON FUNCTIONS TO "pg_write_server_files";

-- grant future schemas from pg_read_all_data to postgres in finance_agent (database: finance_agent)
ALTER DEFAULT PRIVILEGES FOR ROLE "pg_read_all_data" GRANT ALL PRIVILEGES ON SCHEMAS TO "postgres";

-- grant future tables from pg_read_all_data to postgres in finance_agent (database: finance_agent)
ALTER DEFAULT PRIVILEGES FOR ROLE "pg_read_all_data" GRANT ALL PRIVILEGES ON TABLES TO "postgres";

-- grant future sequences from pg_read_all_data to postgres in finance_agent (database: finance_agent)
ALTER DEFAULT PRIVILEGES FOR ROLE "pg_read_all_data" GRANT ALL PRIVILEGES ON SEQUENCES TO "postgres";

-- grant future functions from pg_read_all_data to postgres in finance_agent (database: finance_agent)
ALTER DEFAULT PRIVILEGES FOR ROLE "pg_read_all_data" GRANT ALL PRIVILEGES ON FUNCTIONS TO "postgres";

-- grant future schemas from pg_read_all_stats to pg_checkpoint in finance_agent (database: finance_agent)
ALTER DEFAULT PRIVILEGES FOR ROLE "pg_read_all_stats" GRANT ALL PRIVILEGES ON SCHEMAS TO "pg_checkpoint";

-- grant future tables from pg_read_all_stats to pg_checkpoint in finance_agent (database: finance_agent)
ALTER DEFAULT PRIVILEGES FOR ROLE "pg_read_all_stats" GRANT ALL PRIVILEGES ON TABLES TO "pg_checkpoint";

-- grant future sequences from pg_read_all_stats to pg_checkpoint in finance_agent (database: finance_agent)
ALTER DEFAULT PRIVILEGES FOR ROLE "pg_read_all_stats" GRANT ALL PRIVILEGES ON SEQUENCES TO "pg_checkpoint";

-- grant future functions from pg_read_all_stats to pg_checkpoint in finance_agent (database: finance_agent)
ALTER DEFAULT PRIVILEGES FOR ROLE "pg_read_all_stats" GRANT ALL PRIVILEGES ON FUNCTIONS TO "pg_checkpoint";

-- grant future schemas from pg_read_all_stats to pg_create_subscription in finance_agent (database: finance_agent)
ALTER DEFAULT PRIVILEGES FOR ROLE "pg_read_all_stats" GRANT ALL PRIVILEGES ON SCHEMAS TO "pg_create_subscription";

-- grant future tables from pg_read_all_stats to pg_create_subscription in finance_agent (database: finance_agent)
ALTER DEFAULT PRIVILEGES FOR ROLE "pg_read_all_stats" GRANT ALL PRIVILEGES ON TABLES TO "pg_create_subscription";

-- grant future sequences from pg_read_all_stats to pg_create_subscription in finance_agent (database: finance_agent)
ALTER DEFAULT PRIVILEGES FOR ROLE "pg_read_all_stats" GRANT ALL PRIVILEGES ON SEQUENCES TO "pg_create_subscription";

-- grant future functions from pg_read_all_stats to pg_create_subscription in finance_agent (database: finance_agent)
ALTER DEFAULT PRIVILEGES FOR ROLE "pg_read_all_stats" GRANT ALL PRIVILEGES ON FUNCTIONS TO "pg_create_subscription";

-- grant future schemas from pg_read_all_stats to pg_database_owner in finance_agent (database: finance_agent)
ALTER DEFAULT PRIVILEGES FOR ROLE "pg_read_all_stats" GRANT ALL PRIVILEGES ON SCHEMAS TO "pg_database_owner";

-- grant future tables from pg_read_all_stats to pg_database_owner in finance_agent (database: finance_agent)
ALTER DEFAULT PRIVILEGES FOR ROLE "pg_read_all_stats" GRANT ALL PRIVILEGES ON TABLES TO "pg_database_owner";

-- grant future sequences from pg_read_all_stats to pg_database_owner in finance_agent (database: finance_agent)
ALTER DEFAULT PRIVILEGES FOR ROLE "pg_read_all_stats" GRANT ALL PRIVILEGES ON SEQUENCES TO "pg_database_owner";

-- grant future functions from pg_read_all_stats to pg_database_owner in finance_agent (database: finance_agent)
ALTER DEFAULT PRIVILEGES FOR ROLE "pg_read_all_stats" GRANT ALL PRIVILEGES ON FUNCTIONS TO "pg_database_owner";

-- grant future schemas from pg_read_all_stats to pg_execute_server_program in finance_agent (database: finance_agent)
ALTER DEFAULT PRIVILEGES FOR ROLE "pg_read_all_stats" GRANT ALL PRIVILEGES ON SCHEMAS TO "pg_execute_server_program";

-- grant future tables from pg_read_all_stats to pg_execute_server_program in finance_agent (database: finance_agent)
ALTER DEFAULT PRIVILEGES FOR ROLE "pg_read_all_stats" GRANT ALL PRIVILEGES ON TABLES TO "pg_execute_server_program";

-- grant future sequences from pg_read_all_stats to pg_execute_server_program in finance_agent (database: finance_agent)
ALTER DEFAULT PRIVILEGES FOR ROLE "pg_read_all_stats" GRANT ALL PRIVILEGES ON SEQUENCES TO "pg_execute_server_program";

-- grant future functions from pg_read_all_stats to pg_execute_server_program in finance_agent (database: finance_agent)
ALTER DEFAULT PRIVILEGES FOR ROLE "pg_read_all_stats" GRANT ALL PRIVILEGES ON FUNCTIONS TO "pg_execute_server_program";

-- grant future schemas from pg_read_all_stats to pg_maintain in finance_agent (database: finance_agent)
ALTER DEFAULT PRIVILEGES FOR ROLE "pg_read_all_stats" GRANT ALL PRIVILEGES ON SCHEMAS TO "pg_maintain";

-- grant future tables from pg_read_all_stats to pg_maintain in finance_agent (database: finance_agent)
ALTER DEFAULT PRIVILEGES FOR ROLE "pg_read_all_stats" GRANT ALL PRIVILEGES ON TABLES TO "pg_maintain";

-- grant future sequences from pg_read_all_stats to pg_maintain in finance_agent (database: finance_agent)
ALTER DEFAULT PRIVILEGES FOR ROLE "pg_read_all_stats" GRANT ALL PRIVILEGES ON SEQUENCES TO "pg_maintain";

-- grant future functions from pg_read_all_stats to pg_maintain in finance_agent (database: finance_agent)
ALTER DEFAULT PRIVILEGES FOR ROLE "pg_read_all_stats" GRANT ALL PRIVILEGES ON FUNCTIONS TO "pg_maintain";

-- grant future schemas from pg_read_all_stats to pg_monitor in finance_agent (database: finance_agent)
ALTER DEFAULT PRIVILEGES FOR ROLE "pg_read_all_stats" GRANT ALL PRIVILEGES ON SCHEMAS TO "pg_monitor";

-- grant future tables from pg_read_all_stats to pg_monitor in finance_agent (database: finance_agent)
ALTER DEFAULT PRIVILEGES FOR ROLE "pg_read_all_stats" GRANT ALL PRIVILEGES ON TABLES TO "pg_monitor";

-- grant future sequences from pg_read_all_stats to pg_monitor in finance_agent (database: finance_agent)
ALTER DEFAULT PRIVILEGES FOR ROLE "pg_read_all_stats" GRANT ALL PRIVILEGES ON SEQUENCES TO "pg_monitor";

-- grant future functions from pg_read_all_stats to pg_monitor in finance_agent (database: finance_agent)
ALTER DEFAULT PRIVILEGES FOR ROLE "pg_read_all_stats" GRANT ALL PRIVILEGES ON FUNCTIONS TO "pg_monitor";

-- grant future schemas from pg_read_all_stats to pg_read_all_data in finance_agent (database: finance_agent)
ALTER DEFAULT PRIVILEGES FOR ROLE "pg_read_all_stats" GRANT ALL PRIVILEGES ON SCHEMAS TO "pg_read_all_data";

-- grant future tables from pg_read_all_stats to pg_read_all_data in finance_agent (database: finance_agent)
ALTER DEFAULT PRIVILEGES FOR ROLE "pg_read_all_stats" GRANT ALL PRIVILEGES ON TABLES TO "pg_read_all_data";

-- grant future sequences from pg_read_all_stats to pg_read_all_data in finance_agent (database: finance_agent)
ALTER DEFAULT PRIVILEGES FOR ROLE "pg_read_all_stats" GRANT ALL PRIVILEGES ON SEQUENCES TO "pg_read_all_data";

-- grant future functions from pg_read_all_stats to pg_read_all_data in finance_agent (database: finance_agent)
ALTER DEFAULT PRIVILEGES FOR ROLE "pg_read_all_stats" GRANT ALL PRIVILEGES ON FUNCTIONS TO "pg_read_all_data";

-- grant future schemas from pg_read_all_stats to pg_read_all_settings in finance_agent (database: finance_agent)
ALTER DEFAULT PRIVILEGES FOR ROLE "pg_read_all_stats" GRANT ALL PRIVILEGES ON SCHEMAS TO "pg_read_all_settings";

-- grant future tables from pg_read_all_stats to pg_read_all_settings in finance_agent (database: finance_agent)
ALTER DEFAULT PRIVILEGES FOR ROLE "pg_read_all_stats" GRANT ALL PRIVILEGES ON TABLES TO "pg_read_all_settings";

-- grant future sequences from pg_read_all_stats to pg_read_all_settings in finance_agent (database: finance_agent)
ALTER DEFAULT PRIVILEGES FOR ROLE "pg_read_all_stats" GRANT ALL PRIVILEGES ON SEQUENCES TO "pg_read_all_settings";

-- grant future functions from pg_read_all_stats to pg_read_all_settings in finance_agent (database: finance_agent)
ALTER DEFAULT PRIVILEGES FOR ROLE "pg_read_all_stats" GRANT ALL PRIVILEGES ON FUNCTIONS TO "pg_read_all_settings";

-- grant future schemas from pg_read_all_stats to pg_read_server_files in finance_agent (database: finance_agent)
ALTER DEFAULT PRIVILEGES FOR ROLE "pg_read_all_stats" GRANT ALL PRIVILEGES ON SCHEMAS TO "pg_read_server_files";

-- grant future tables from pg_read_all_stats to pg_read_server_files in finance_agent (database: finance_agent)
ALTER DEFAULT PRIVILEGES FOR ROLE "pg_read_all_stats" GRANT ALL PRIVILEGES ON TABLES TO "pg_read_server_files";

-- grant future sequences from pg_read_all_stats to pg_read_server_files in finance_agent (database: finance_agent)
ALTER DEFAULT PRIVILEGES FOR ROLE "pg_read_all_stats" GRANT ALL PRIVILEGES ON SEQUENCES TO "pg_read_server_files";

-- grant future functions from pg_read_all_stats to pg_read_server_files in finance_agent (database: finance_agent)
ALTER DEFAULT PRIVILEGES FOR ROLE "pg_read_all_stats" GRANT ALL PRIVILEGES ON FUNCTIONS TO "pg_read_server_files";

-- grant future schemas from pg_read_all_stats to pg_signal_backend in finance_agent (database: finance_agent)
ALTER DEFAULT PRIVILEGES FOR ROLE "pg_read_all_stats" GRANT ALL PRIVILEGES ON SCHEMAS TO "pg_signal_backend";

-- grant future tables from pg_read_all_stats to pg_signal_backend in finance_agent (database: finance_agent)
ALTER DEFAULT PRIVILEGES FOR ROLE "pg_read_all_stats" GRANT ALL PRIVILEGES ON TABLES TO "pg_signal_backend";

-- grant future sequences from pg_read_all_stats to pg_signal_backend in finance_agent (database: finance_agent)
ALTER DEFAULT PRIVILEGES FOR ROLE "pg_read_all_stats" GRANT ALL PRIVILEGES ON SEQUENCES TO "pg_signal_backend";

-- grant future functions from pg_read_all_stats to pg_signal_backend in finance_agent (database: finance_agent)
ALTER DEFAULT PRIVILEGES FOR ROLE "pg_read_all_stats" GRANT ALL PRIVILEGES ON FUNCTIONS TO "pg_signal_backend";

-- grant future schemas from pg_read_all_stats to pg_stat_scan_tables in finance_agent (database: finance_agent)
ALTER DEFAULT PRIVILEGES FOR ROLE "pg_read_all_stats" GRANT ALL PRIVILEGES ON SCHEMAS TO "pg_stat_scan_tables";

-- grant future tables from pg_read_all_stats to pg_stat_scan_tables in finance_agent (database: finance_agent)
ALTER DEFAULT PRIVILEGES FOR ROLE "pg_read_all_stats" GRANT ALL PRIVILEGES ON TABLES TO "pg_stat_scan_tables";

-- grant future sequences from pg_read_all_stats to pg_stat_scan_tables in finance_agent (database: finance_agent)
ALTER DEFAULT PRIVILEGES FOR ROLE "pg_read_all_stats" GRANT ALL PRIVILEGES ON SEQUENCES TO "pg_stat_scan_tables";

-- grant future functions from pg_read_all_stats to pg_stat_scan_tables in finance_agent (database: finance_agent)
ALTER DEFAULT PRIVILEGES FOR ROLE "pg_read_all_stats" GRANT ALL PRIVILEGES ON FUNCTIONS TO "pg_stat_scan_tables";

-- grant future schemas from pg_read_all_stats to pg_use_reserved_connections in finance_agent (database: finance_agent)
ALTER DEFAULT PRIVILEGES FOR ROLE "pg_read_all_stats" GRANT ALL PRIVILEGES ON SCHEMAS TO "pg_use_reserved_connections";

-- grant future tables from pg_read_all_stats to pg_use_reserved_connections in finance_agent (database: finance_agent)
ALTER DEFAULT PRIVILEGES FOR ROLE "pg_read_all_stats" GRANT ALL PRIVILEGES ON TABLES TO "pg_use_reserved_connections";

-- grant future sequences from pg_read_all_stats to pg_use_reserved_connections in finance_agent (database: finance_agent)
ALTER DEFAULT PRIVILEGES FOR ROLE "pg_read_all_stats" GRANT ALL PRIVILEGES ON SEQUENCES TO "pg_use_reserved_connections";

-- grant future functions from pg_read_all_stats to pg_use_reserved_connections in finance_agent (database: finance_agent)
ALTER DEFAULT PRIVILEGES FOR ROLE "pg_read_all_stats" GRANT ALL PRIVILEGES ON FUNCTIONS TO "pg_use_reserved_connections";

-- grant future schemas from pg_read_all_stats to pg_write_all_data in finance_agent (database: finance_agent)
ALTER DEFAULT PRIVILEGES FOR ROLE "pg_read_all_stats" GRANT ALL PRIVILEGES ON SCHEMAS TO "pg_write_all_data";

-- grant future tables from pg_read_all_stats to pg_write_all_data in finance_agent (database: finance_agent)
ALTER DEFAULT PRIVILEGES FOR ROLE "pg_read_all_stats" GRANT ALL PRIVILEGES ON TABLES TO "pg_write_all_data";

-- grant future sequences from pg_read_all_stats to pg_write_all_data in finance_agent (database: finance_agent)
ALTER DEFAULT PRIVILEGES FOR ROLE "pg_read_all_stats" GRANT ALL PRIVILEGES ON SEQUENCES TO "pg_write_all_data";

-- grant future functions from pg_read_all_stats to pg_write_all_data in finance_agent (database: finance_agent)
ALTER DEFAULT PRIVILEGES FOR ROLE "pg_read_all_stats" GRANT ALL PRIVILEGES ON FUNCTIONS TO "pg_write_all_data";

-- grant future schemas from pg_read_all_stats to pg_write_server_files in finance_agent (database: finance_agent)
ALTER DEFAULT PRIVILEGES FOR ROLE "pg_read_all_stats" GRANT ALL PRIVILEGES ON SCHEMAS TO "pg_write_server_files";

-- grant future tables from pg_read_all_stats to pg_write_server_files in finance_agent (database: finance_agent)
ALTER DEFAULT PRIVILEGES FOR ROLE "pg_read_all_stats" GRANT ALL PRIVILEGES ON TABLES TO "pg_write_server_files";

-- grant future sequences from pg_read_all_stats to pg_write_server_files in finance_agent (database: finance_agent)
ALTER DEFAULT PRIVILEGES FOR ROLE "pg_read_all_stats" GRANT ALL PRIVILEGES ON SEQUENCES TO "pg_write_server_files";

-- grant future functions from pg_read_all_stats to pg_write_server_files in finance_agent (database: finance_agent)
ALTER DEFAULT PRIVILEGES FOR ROLE "pg_read_all_stats" GRANT ALL PRIVILEGES ON FUNCTIONS TO "pg_write_server_files";

-- grant future schemas from pg_read_all_stats to postgres in finance_agent (database: finance_agent)
ALTER DEFAULT PRIVILEGES FOR ROLE "pg_read_all_stats" GRANT ALL PRIVILEGES ON SCHEMAS TO "postgres";

-- grant future tables from pg_read_all_stats to postgres in finance_agent (database: finance_agent)
ALTER DEFAULT PRIVILEGES FOR ROLE "pg_read_all_stats" GRANT ALL PRIVILEGES ON TABLES TO "postgres";

-- grant future sequences from pg_read_all_stats to postgres in finance_agent (database: finance_agent)
ALTER DEFAULT PRIVILEGES FOR ROLE "pg_read_all_stats" GRANT ALL PRIVILEGES ON SEQUENCES TO "postgres";

-- grant future functions from pg_read_all_stats to postgres in finance_agent (database: finance_agent)
ALTER DEFAULT PRIVILEGES FOR ROLE "pg_read_all_stats" GRANT ALL PRIVILEGES ON FUNCTIONS TO "postgres";

-- grant future schemas from pg_read_all_settings to pg_checkpoint in finance_agent (database: finance_agent)
ALTER DEFAULT PRIVILEGES FOR ROLE "pg_read_all_settings" GRANT ALL PRIVILEGES ON SCHEMAS TO "pg_checkpoint";

-- grant future tables from pg_read_all_settings to pg_checkpoint in finance_agent (database: finance_agent)
ALTER DEFAULT PRIVILEGES FOR ROLE "pg_read_all_settings" GRANT ALL PRIVILEGES ON TABLES TO "pg_checkpoint";

-- grant future sequences from pg_read_all_settings to pg_checkpoint in finance_agent (database: finance_agent)
ALTER DEFAULT PRIVILEGES FOR ROLE "pg_read_all_settings" GRANT ALL PRIVILEGES ON SEQUENCES TO "pg_checkpoint";

-- grant future functions from pg_read_all_settings to pg_checkpoint in finance_agent (database: finance_agent)
ALTER DEFAULT PRIVILEGES FOR ROLE "pg_read_all_settings" GRANT ALL PRIVILEGES ON FUNCTIONS TO "pg_checkpoint";

-- grant future schemas from pg_read_all_settings to pg_create_subscription in finance_agent (database: finance_agent)
ALTER DEFAULT PRIVILEGES FOR ROLE "pg_read_all_settings" GRANT ALL PRIVILEGES ON SCHEMAS TO "pg_create_subscription";

-- grant future tables from pg_read_all_settings to pg_create_subscription in finance_agent (database: finance_agent)
ALTER DEFAULT PRIVILEGES FOR ROLE "pg_read_all_settings" GRANT ALL PRIVILEGES ON TABLES TO "pg_create_subscription";

-- grant future sequences from pg_read_all_settings to pg_create_subscription in finance_agent (database: finance_agent)
ALTER DEFAULT PRIVILEGES FOR ROLE "pg_read_all_settings" GRANT ALL PRIVILEGES ON SEQUENCES TO "pg_create_subscription";

-- grant future functions from pg_read_all_settings to pg_create_subscription in finance_agent (database: finance_agent)
ALTER DEFAULT PRIVILEGES FOR ROLE "pg_read_all_settings" GRANT ALL PRIVILEGES ON FUNCTIONS TO "pg_create_subscription";

-- grant future schemas from pg_read_all_settings to pg_database_owner in finance_agent (database: finance_agent)
ALTER DEFAULT PRIVILEGES FOR ROLE "pg_read_all_settings" GRANT ALL PRIVILEGES ON SCHEMAS TO "pg_database_owner";

-- grant future tables from pg_read_all_settings to pg_database_owner in finance_agent (database: finance_agent)
ALTER DEFAULT PRIVILEGES FOR ROLE "pg_read_all_settings" GRANT ALL PRIVILEGES ON TABLES TO "pg_database_owner";

-- grant future sequences from pg_read_all_settings to pg_database_owner in finance_agent (database: finance_agent)
ALTER DEFAULT PRIVILEGES FOR ROLE "pg_read_all_settings" GRANT ALL PRIVILEGES ON SEQUENCES TO "pg_database_owner";

-- grant future functions from pg_read_all_settings to pg_database_owner in finance_agent (database: finance_agent)
ALTER DEFAULT PRIVILEGES FOR ROLE "pg_read_all_settings" GRANT ALL PRIVILEGES ON FUNCTIONS TO "pg_database_owner";

-- grant future schemas from pg_read_all_settings to pg_execute_server_program in finance_agent (database: finance_agent)
ALTER DEFAULT PRIVILEGES FOR ROLE "pg_read_all_settings" GRANT ALL PRIVILEGES ON SCHEMAS TO "pg_execute_server_program";

-- grant future tables from pg_read_all_settings to pg_execute_server_program in finance_agent (database: finance_agent)
ALTER DEFAULT PRIVILEGES FOR ROLE "pg_read_all_settings" GRANT ALL PRIVILEGES ON TABLES TO "pg_execute_server_program";

-- grant future sequences from pg_read_all_settings to pg_execute_server_program in finance_agent (database: finance_agent)
ALTER DEFAULT PRIVILEGES FOR ROLE "pg_read_all_settings" GRANT ALL PRIVILEGES ON SEQUENCES TO "pg_execute_server_program";

-- grant future functions from pg_read_all_settings to pg_execute_server_program in finance_agent (database: finance_agent)
ALTER DEFAULT PRIVILEGES FOR ROLE "pg_read_all_settings" GRANT ALL PRIVILEGES ON FUNCTIONS TO "pg_execute_server_program";

-- grant future schemas from pg_read_all_settings to pg_maintain in finance_agent (database: finance_agent)
ALTER DEFAULT PRIVILEGES FOR ROLE "pg_read_all_settings" GRANT ALL PRIVILEGES ON SCHEMAS TO "pg_maintain";

-- grant future tables from pg_read_all_settings to pg_maintain in finance_agent (database: finance_agent)
ALTER DEFAULT PRIVILEGES FOR ROLE "pg_read_all_settings" GRANT ALL PRIVILEGES ON TABLES TO "pg_maintain";

-- grant future sequences from pg_read_all_settings to pg_maintain in finance_agent (database: finance_agent)
ALTER DEFAULT PRIVILEGES FOR ROLE "pg_read_all_settings" GRANT ALL PRIVILEGES ON SEQUENCES TO "pg_maintain";

-- grant future functions from pg_read_all_settings to pg_maintain in finance_agent (database: finance_agent)
ALTER DEFAULT PRIVILEGES FOR ROLE "pg_read_all_settings" GRANT ALL PRIVILEGES ON FUNCTIONS TO "pg_maintain";

-- grant future schemas from pg_read_all_settings to pg_monitor in finance_agent (database: finance_agent)
ALTER DEFAULT PRIVILEGES FOR ROLE "pg_read_all_settings" GRANT ALL PRIVILEGES ON SCHEMAS TO "pg_monitor";

-- grant future tables from pg_read_all_settings to pg_monitor in finance_agent (database: finance_agent)
ALTER DEFAULT PRIVILEGES FOR ROLE "pg_read_all_settings" GRANT ALL PRIVILEGES ON TABLES TO "pg_monitor";

-- grant future sequences from pg_read_all_settings to pg_monitor in finance_agent (database: finance_agent)
ALTER DEFAULT PRIVILEGES FOR ROLE "pg_read_all_settings" GRANT ALL PRIVILEGES ON SEQUENCES TO "pg_monitor";

-- grant future functions from pg_read_all_settings to pg_monitor in finance_agent (database: finance_agent)
ALTER DEFAULT PRIVILEGES FOR ROLE "pg_read_all_settings" GRANT ALL PRIVILEGES ON FUNCTIONS TO "pg_monitor";

-- grant future schemas from pg_read_all_settings to pg_read_all_data in finance_agent (database: finance_agent)
ALTER DEFAULT PRIVILEGES FOR ROLE "pg_read_all_settings" GRANT ALL PRIVILEGES ON SCHEMAS TO "pg_read_all_data";

-- grant future tables from pg_read_all_settings to pg_read_all_data in finance_agent (database: finance_agent)
ALTER DEFAULT PRIVILEGES FOR ROLE "pg_read_all_settings" GRANT ALL PRIVILEGES ON TABLES TO "pg_read_all_data";

-- grant future sequences from pg_read_all_settings to pg_read_all_data in finance_agent (database: finance_agent)
ALTER DEFAULT PRIVILEGES FOR ROLE "pg_read_all_settings" GRANT ALL PRIVILEGES ON SEQUENCES TO "pg_read_all_data";

-- grant future functions from pg_read_all_settings to pg_read_all_data in finance_agent (database: finance_agent)
ALTER DEFAULT PRIVILEGES FOR ROLE "pg_read_all_settings" GRANT ALL PRIVILEGES ON FUNCTIONS TO "pg_read_all_data";

-- grant future schemas from pg_read_all_settings to pg_read_all_stats in finance_agent (database: finance_agent)
ALTER DEFAULT PRIVILEGES FOR ROLE "pg_read_all_settings" GRANT ALL PRIVILEGES ON SCHEMAS TO "pg_read_all_stats";

-- grant future tables from pg_read_all_settings to pg_read_all_stats in finance_agent (database: finance_agent)
ALTER DEFAULT PRIVILEGES FOR ROLE "pg_read_all_settings" GRANT ALL PRIVILEGES ON TABLES TO "pg_read_all_stats";

-- grant future sequences from pg_read_all_settings to pg_read_all_stats in finance_agent (database: finance_agent)
ALTER DEFAULT PRIVILEGES FOR ROLE "pg_read_all_settings" GRANT ALL PRIVILEGES ON SEQUENCES TO "pg_read_all_stats";

-- grant future functions from pg_read_all_settings to pg_read_all_stats in finance_agent (database: finance_agent)
ALTER DEFAULT PRIVILEGES FOR ROLE "pg_read_all_settings" GRANT ALL PRIVILEGES ON FUNCTIONS TO "pg_read_all_stats";

-- grant future schemas from pg_read_all_settings to pg_read_server_files in finance_agent (database: finance_agent)
ALTER DEFAULT PRIVILEGES FOR ROLE "pg_read_all_settings" GRANT ALL PRIVILEGES ON SCHEMAS TO "pg_read_server_files";

-- grant future tables from pg_read_all_settings to pg_read_server_files in finance_agent (database: finance_agent)
ALTER DEFAULT PRIVILEGES FOR ROLE "pg_read_all_settings" GRANT ALL PRIVILEGES ON TABLES TO "pg_read_server_files";

-- grant future sequences from pg_read_all_settings to pg_read_server_files in finance_agent (database: finance_agent)
ALTER DEFAULT PRIVILEGES FOR ROLE "pg_read_all_settings" GRANT ALL PRIVILEGES ON SEQUENCES TO "pg_read_server_files";

-- grant future functions from pg_read_all_settings to pg_read_server_files in finance_agent (database: finance_agent)
ALTER DEFAULT PRIVILEGES FOR ROLE "pg_read_all_settings" GRANT ALL PRIVILEGES ON FUNCTIONS TO "pg_read_server_files";

-- grant future schemas from pg_read_all_settings to pg_signal_backend in finance_agent (database: finance_agent)
ALTER DEFAULT PRIVILEGES FOR ROLE "pg_read_all_settings" GRANT ALL PRIVILEGES ON SCHEMAS TO "pg_signal_backend";

-- grant future tables from pg_read_all_settings to pg_signal_backend in finance_agent (database: finance_agent)
ALTER DEFAULT PRIVILEGES FOR ROLE "pg_read_all_settings" GRANT ALL PRIVILEGES ON TABLES TO "pg_signal_backend";

-- grant future sequences from pg_read_all_settings to pg_signal_backend in finance_agent (database: finance_agent)
ALTER DEFAULT PRIVILEGES FOR ROLE "pg_read_all_settings" GRANT ALL PRIVILEGES ON SEQUENCES TO "pg_signal_backend";

-- grant future functions from pg_read_all_settings to pg_signal_backend in finance_agent (database: finance_agent)
ALTER DEFAULT PRIVILEGES FOR ROLE "pg_read_all_settings" GRANT ALL PRIVILEGES ON FUNCTIONS TO "pg_signal_backend";

-- grant future schemas from pg_read_all_settings to pg_stat_scan_tables in finance_agent (database: finance_agent)
ALTER DEFAULT PRIVILEGES FOR ROLE "pg_read_all_settings" GRANT ALL PRIVILEGES ON SCHEMAS TO "pg_stat_scan_tables";

-- grant future tables from pg_read_all_settings to pg_stat_scan_tables in finance_agent (database: finance_agent)
ALTER DEFAULT PRIVILEGES FOR ROLE "pg_read_all_settings" GRANT ALL PRIVILEGES ON TABLES TO "pg_stat_scan_tables";

-- grant future sequences from pg_read_all_settings to pg_stat_scan_tables in finance_agent (database: finance_agent)
ALTER DEFAULT PRIVILEGES FOR ROLE "pg_read_all_settings" GRANT ALL PRIVILEGES ON SEQUENCES TO "pg_stat_scan_tables";

-- grant future functions from pg_read_all_settings to pg_stat_scan_tables in finance_agent (database: finance_agent)
ALTER DEFAULT PRIVILEGES FOR ROLE "pg_read_all_settings" GRANT ALL PRIVILEGES ON FUNCTIONS TO "pg_stat_scan_tables";

-- grant future schemas from pg_read_all_settings to pg_use_reserved_connections in finance_agent (database: finance_agent)
ALTER DEFAULT PRIVILEGES FOR ROLE "pg_read_all_settings" GRANT ALL PRIVILEGES ON SCHEMAS TO "pg_use_reserved_connections";

-- grant future tables from pg_read_all_settings to pg_use_reserved_connections in finance_agent (database: finance_agent)
ALTER DEFAULT PRIVILEGES FOR ROLE "pg_read_all_settings" GRANT ALL PRIVILEGES ON TABLES TO "pg_use_reserved_connections";

-- grant future sequences from pg_read_all_settings to pg_use_reserved_connections in finance_agent (database: finance_agent)
ALTER DEFAULT PRIVILEGES FOR ROLE "pg_read_all_settings" GRANT ALL PRIVILEGES ON SEQUENCES TO "pg_use_reserved_connections";

-- grant future functions from pg_read_all_settings to pg_use_reserved_connections in finance_agent (database: finance_agent)
ALTER DEFAULT PRIVILEGES FOR ROLE "pg_read_all_settings" GRANT ALL PRIVILEGES ON FUNCTIONS TO "pg_use_reserved_connections";

-- grant future schemas from pg_read_all_settings to pg_write_all_data in finance_agent (database: finance_agent)
ALTER DEFAULT PRIVILEGES FOR ROLE "pg_read_all_settings" GRANT ALL PRIVILEGES ON SCHEMAS TO "pg_write_all_data";

-- grant future tables from pg_read_all_settings to pg_write_all_data in finance_agent (database: finance_agent)
ALTER DEFAULT PRIVILEGES FOR ROLE "pg_read_all_settings" GRANT ALL PRIVILEGES ON TABLES TO "pg_write_all_data";

-- grant future sequences from pg_read_all_settings to pg_write_all_data in finance_agent (database: finance_agent)
ALTER DEFAULT PRIVILEGES FOR ROLE "pg_read_all_settings" GRANT ALL PRIVILEGES ON SEQUENCES TO "pg_write_all_data";

-- grant future functions from pg_read_all_settings to pg_write_all_data in finance_agent (database: finance_agent)
ALTER DEFAULT PRIVILEGES FOR ROLE "pg_read_all_settings" GRANT ALL PRIVILEGES ON FUNCTIONS TO "pg_write_all_data";

-- grant future schemas from pg_read_all_settings to pg_write_server_files in finance_agent (database: finance_agent)
ALTER DEFAULT PRIVILEGES FOR ROLE "pg_read_all_settings" GRANT ALL PRIVILEGES ON SCHEMAS TO "pg_write_server_files";

-- grant future tables from pg_read_all_settings to pg_write_server_files in finance_agent (database: finance_agent)
ALTER DEFAULT PRIVILEGES FOR ROLE "pg_read_all_settings" GRANT ALL PRIVILEGES ON TABLES TO "pg_write_server_files";

-- grant future sequences from pg_read_all_settings to pg_write_server_files in finance_agent (database: finance_agent)
ALTER DEFAULT PRIVILEGES FOR ROLE "pg_read_all_settings" GRANT ALL PRIVILEGES ON SEQUENCES TO "pg_write_server_files";

-- grant future functions from pg_read_all_settings to pg_write_server_files in finance_agent (database: finance_agent)
ALTER DEFAULT PRIVILEGES FOR ROLE "pg_read_all_settings" GRANT ALL PRIVILEGES ON FUNCTIONS TO "pg_write_server_files";

-- grant future schemas from pg_read_all_settings to postgres in finance_agent (database: finance_agent)
ALTER DEFAULT PRIVILEGES FOR ROLE "pg_read_all_settings" GRANT ALL PRIVILEGES ON SCHEMAS TO "postgres";

-- grant future tables from pg_read_all_settings to postgres in finance_agent (database: finance_agent)
ALTER DEFAULT PRIVILEGES FOR ROLE "pg_read_all_settings" GRANT ALL PRIVILEGES ON TABLES TO "postgres";

-- grant future sequences from pg_read_all_settings to postgres in finance_agent (database: finance_agent)
ALTER DEFAULT PRIVILEGES FOR ROLE "pg_read_all_settings" GRANT ALL PRIVILEGES ON SEQUENCES TO "postgres";

-- grant future functions from pg_read_all_settings to postgres in finance_agent (database: finance_agent)
ALTER DEFAULT PRIVILEGES FOR ROLE "pg_read_all_settings" GRANT ALL PRIVILEGES ON FUNCTIONS TO "postgres";

-- grant future schemas from pg_read_server_files to pg_checkpoint in finance_agent (database: finance_agent)
ALTER DEFAULT PRIVILEGES FOR ROLE "pg_read_server_files" GRANT ALL PRIVILEGES ON SCHEMAS TO "pg_checkpoint";

-- grant future tables from pg_read_server_files to pg_checkpoint in finance_agent (database: finance_agent)
ALTER DEFAULT PRIVILEGES FOR ROLE "pg_read_server_files" GRANT ALL PRIVILEGES ON TABLES TO "pg_checkpoint";

-- grant future sequences from pg_read_server_files to pg_checkpoint in finance_agent (database: finance_agent)
ALTER DEFAULT PRIVILEGES FOR ROLE "pg_read_server_files" GRANT ALL PRIVILEGES ON SEQUENCES TO "pg_checkpoint";

-- grant future functions from pg_read_server_files to pg_checkpoint in finance_agent (database: finance_agent)
ALTER DEFAULT PRIVILEGES FOR ROLE "pg_read_server_files" GRANT ALL PRIVILEGES ON FUNCTIONS TO "pg_checkpoint";

-- grant future schemas from pg_read_server_files to pg_create_subscription in finance_agent (database: finance_agent)
ALTER DEFAULT PRIVILEGES FOR ROLE "pg_read_server_files" GRANT ALL PRIVILEGES ON SCHEMAS TO "pg_create_subscription";

-- grant future tables from pg_read_server_files to pg_create_subscription in finance_agent (database: finance_agent)
ALTER DEFAULT PRIVILEGES FOR ROLE "pg_read_server_files" GRANT ALL PRIVILEGES ON TABLES TO "pg_create_subscription";

-- grant future sequences from pg_read_server_files to pg_create_subscription in finance_agent (database: finance_agent)
ALTER DEFAULT PRIVILEGES FOR ROLE "pg_read_server_files" GRANT ALL PRIVILEGES ON SEQUENCES TO "pg_create_subscription";

-- grant future functions from pg_read_server_files to pg_create_subscription in finance_agent (database: finance_agent)
ALTER DEFAULT PRIVILEGES FOR ROLE "pg_read_server_files" GRANT ALL PRIVILEGES ON FUNCTIONS TO "pg_create_subscription";

-- grant future schemas from pg_read_server_files to pg_database_owner in finance_agent (database: finance_agent)
ALTER DEFAULT PRIVILEGES FOR ROLE "pg_read_server_files" GRANT ALL PRIVILEGES ON SCHEMAS TO "pg_database_owner";

-- grant future tables from pg_read_server_files to pg_database_owner in finance_agent (database: finance_agent)
ALTER DEFAULT PRIVILEGES FOR ROLE "pg_read_server_files" GRANT ALL PRIVILEGES ON TABLES TO "pg_database_owner";

-- grant future sequences from pg_read_server_files to pg_database_owner in finance_agent (database: finance_agent)
ALTER DEFAULT PRIVILEGES FOR ROLE "pg_read_server_files" GRANT ALL PRIVILEGES ON SEQUENCES TO "pg_database_owner";

-- grant future functions from pg_read_server_files to pg_database_owner in finance_agent (database: finance_agent)
ALTER DEFAULT PRIVILEGES FOR ROLE "pg_read_server_files" GRANT ALL PRIVILEGES ON FUNCTIONS TO "pg_database_owner";

-- grant future schemas from pg_read_server_files to pg_execute_server_program in finance_agent (database: finance_agent)
ALTER DEFAULT PRIVILEGES FOR ROLE "pg_read_server_files" GRANT ALL PRIVILEGES ON SCHEMAS TO "pg_execute_server_program";

-- grant future tables from pg_read_server_files to pg_execute_server_program in finance_agent (database: finance_agent)
ALTER DEFAULT PRIVILEGES FOR ROLE "pg_read_server_files" GRANT ALL PRIVILEGES ON TABLES TO "pg_execute_server_program";

-- grant future sequences from pg_read_server_files to pg_execute_server_program in finance_agent (database: finance_agent)
ALTER DEFAULT PRIVILEGES FOR ROLE "pg_read_server_files" GRANT ALL PRIVILEGES ON SEQUENCES TO "pg_execute_server_program";

-- grant future functions from pg_read_server_files to pg_execute_server_program in finance_agent (database: finance_agent)
ALTER DEFAULT PRIVILEGES FOR ROLE "pg_read_server_files" GRANT ALL PRIVILEGES ON FUNCTIONS TO "pg_execute_server_program";

-- grant future schemas from pg_read_server_files to pg_maintain in finance_agent (database: finance_agent)
ALTER DEFAULT PRIVILEGES FOR ROLE "pg_read_server_files" GRANT ALL PRIVILEGES ON SCHEMAS TO "pg_maintain";

-- grant future tables from pg_read_server_files to pg_maintain in finance_agent (database: finance_agent)
ALTER DEFAULT PRIVILEGES FOR ROLE "pg_read_server_files" GRANT ALL PRIVILEGES ON TABLES TO "pg_maintain";

-- grant future sequences from pg_read_server_files to pg_maintain in finance_agent (database: finance_agent)
ALTER DEFAULT PRIVILEGES FOR ROLE "pg_read_server_files" GRANT ALL PRIVILEGES ON SEQUENCES TO "pg_maintain";

-- grant future functions from pg_read_server_files to pg_maintain in finance_agent (database: finance_agent)
ALTER DEFAULT PRIVILEGES FOR ROLE "pg_read_server_files" GRANT ALL PRIVILEGES ON FUNCTIONS TO "pg_maintain";

-- grant future schemas from pg_read_server_files to pg_monitor in finance_agent (database: finance_agent)
ALTER DEFAULT PRIVILEGES FOR ROLE "pg_read_server_files" GRANT ALL PRIVILEGES ON SCHEMAS TO "pg_monitor";

-- grant future tables from pg_read_server_files to pg_monitor in finance_agent (database: finance_agent)
ALTER DEFAULT PRIVILEGES FOR ROLE "pg_read_server_files" GRANT ALL PRIVILEGES ON TABLES TO "pg_monitor";

-- grant future sequences from pg_read_server_files to pg_monitor in finance_agent (database: finance_agent)
ALTER DEFAULT PRIVILEGES FOR ROLE "pg_read_server_files" GRANT ALL PRIVILEGES ON SEQUENCES TO "pg_monitor";

-- grant future functions from pg_read_server_files to pg_monitor in finance_agent (database: finance_agent)
ALTER DEFAULT PRIVILEGES FOR ROLE "pg_read_server_files" GRANT ALL PRIVILEGES ON FUNCTIONS TO "pg_monitor";

-- grant future schemas from pg_read_server_files to pg_read_all_data in finance_agent (database: finance_agent)
ALTER DEFAULT PRIVILEGES FOR ROLE "pg_read_server_files" GRANT ALL PRIVILEGES ON SCHEMAS TO "pg_read_all_data";

-- grant future tables from pg_read_server_files to pg_read_all_data in finance_agent (database: finance_agent)
ALTER DEFAULT PRIVILEGES FOR ROLE "pg_read_server_files" GRANT ALL PRIVILEGES ON TABLES TO "pg_read_all_data";

-- grant future sequences from pg_read_server_files to pg_read_all_data in finance_agent (database: finance_agent)
ALTER DEFAULT PRIVILEGES FOR ROLE "pg_read_server_files" GRANT ALL PRIVILEGES ON SEQUENCES TO "pg_read_all_data";

-- grant future functions from pg_read_server_files to pg_read_all_data in finance_agent (database: finance_agent)
ALTER DEFAULT PRIVILEGES FOR ROLE "pg_read_server_files" GRANT ALL PRIVILEGES ON FUNCTIONS TO "pg_read_all_data";

-- grant future schemas from pg_read_server_files to pg_read_all_stats in finance_agent (database: finance_agent)
ALTER DEFAULT PRIVILEGES FOR ROLE "pg_read_server_files" GRANT ALL PRIVILEGES ON SCHEMAS TO "pg_read_all_stats";

-- grant future tables from pg_read_server_files to pg_read_all_stats in finance_agent (database: finance_agent)
ALTER DEFAULT PRIVILEGES FOR ROLE "pg_read_server_files" GRANT ALL PRIVILEGES ON TABLES TO "pg_read_all_stats";

-- grant future sequences from pg_read_server_files to pg_read_all_stats in finance_agent (database: finance_agent)
ALTER DEFAULT PRIVILEGES FOR ROLE "pg_read_server_files" GRANT ALL PRIVILEGES ON SEQUENCES TO "pg_read_all_stats";

-- grant future functions from pg_read_server_files to pg_read_all_stats in finance_agent (database: finance_agent)
ALTER DEFAULT PRIVILEGES FOR ROLE "pg_read_server_files" GRANT ALL PRIVILEGES ON FUNCTIONS TO "pg_read_all_stats";

-- grant future schemas from pg_read_server_files to pg_read_all_settings in finance_agent (database: finance_agent)
ALTER DEFAULT PRIVILEGES FOR ROLE "pg_read_server_files" GRANT ALL PRIVILEGES ON SCHEMAS TO "pg_read_all_settings";

-- grant future tables from pg_read_server_files to pg_read_all_settings in finance_agent (database: finance_agent)
ALTER DEFAULT PRIVILEGES FOR ROLE "pg_read_server_files" GRANT ALL PRIVILEGES ON TABLES TO "pg_read_all_settings";

-- grant future sequences from pg_read_server_files to pg_read_all_settings in finance_agent (database: finance_agent)
ALTER DEFAULT PRIVILEGES FOR ROLE "pg_read_server_files" GRANT ALL PRIVILEGES ON SEQUENCES TO "pg_read_all_settings";

-- grant future functions from pg_read_server_files to pg_read_all_settings in finance_agent (database: finance_agent)
ALTER DEFAULT PRIVILEGES FOR ROLE "pg_read_server_files" GRANT ALL PRIVILEGES ON FUNCTIONS TO "pg_read_all_settings";

-- grant future schemas from pg_read_server_files to pg_signal_backend in finance_agent (database: finance_agent)
ALTER DEFAULT PRIVILEGES FOR ROLE "pg_read_server_files" GRANT ALL PRIVILEGES ON SCHEMAS TO "pg_signal_backend";

-- grant future tables from pg_read_server_files to pg_signal_backend in finance_agent (database: finance_agent)
ALTER DEFAULT PRIVILEGES FOR ROLE "pg_read_server_files" GRANT ALL PRIVILEGES ON TABLES TO "pg_signal_backend";

-- grant future sequences from pg_read_server_files to pg_signal_backend in finance_agent (database: finance_agent)
ALTER DEFAULT PRIVILEGES FOR ROLE "pg_read_server_files" GRANT ALL PRIVILEGES ON SEQUENCES TO "pg_signal_backend";

-- grant future functions from pg_read_server_files to pg_signal_backend in finance_agent (database: finance_agent)
ALTER DEFAULT PRIVILEGES FOR ROLE "pg_read_server_files" GRANT ALL PRIVILEGES ON FUNCTIONS TO "pg_signal_backend";

-- grant future schemas from pg_read_server_files to pg_stat_scan_tables in finance_agent (database: finance_agent)
ALTER DEFAULT PRIVILEGES FOR ROLE "pg_read_server_files" GRANT ALL PRIVILEGES ON SCHEMAS TO "pg_stat_scan_tables";

-- grant future tables from pg_read_server_files to pg_stat_scan_tables in finance_agent (database: finance_agent)
ALTER DEFAULT PRIVILEGES FOR ROLE "pg_read_server_files" GRANT ALL PRIVILEGES ON TABLES TO "pg_stat_scan_tables";

-- grant future sequences from pg_read_server_files to pg_stat_scan_tables in finance_agent (database: finance_agent)
ALTER DEFAULT PRIVILEGES FOR ROLE "pg_read_server_files" GRANT ALL PRIVILEGES ON SEQUENCES TO "pg_stat_scan_tables";

-- grant future functions from pg_read_server_files to pg_stat_scan_tables in finance_agent (database: finance_agent)
ALTER DEFAULT PRIVILEGES FOR ROLE "pg_read_server_files" GRANT ALL PRIVILEGES ON FUNCTIONS TO "pg_stat_scan_tables";

-- grant future schemas from pg_read_server_files to pg_use_reserved_connections in finance_agent (database: finance_agent)
ALTER DEFAULT PRIVILEGES FOR ROLE "pg_read_server_files" GRANT ALL PRIVILEGES ON SCHEMAS TO "pg_use_reserved_connections";

-- grant future tables from pg_read_server_files to pg_use_reserved_connections in finance_agent (database: finance_agent)
ALTER DEFAULT PRIVILEGES FOR ROLE "pg_read_server_files" GRANT ALL PRIVILEGES ON TABLES TO "pg_use_reserved_connections";

-- grant future sequences from pg_read_server_files to pg_use_reserved_connections in finance_agent (database: finance_agent)
ALTER DEFAULT PRIVILEGES FOR ROLE "pg_read_server_files" GRANT ALL PRIVILEGES ON SEQUENCES TO "pg_use_reserved_connections";

-- grant future functions from pg_read_server_files to pg_use_reserved_connections in finance_agent (database: finance_agent)
ALTER DEFAULT PRIVILEGES FOR ROLE "pg_read_server_files" GRANT ALL PRIVILEGES ON FUNCTIONS TO "pg_use_reserved_connections";

-- grant future schemas from pg_read_server_files to pg_write_all_data in finance_agent (database: finance_agent)
ALTER DEFAULT PRIVILEGES FOR ROLE "pg_read_server_files" GRANT ALL PRIVILEGES ON SCHEMAS TO "pg_write_all_data";

-- grant future tables from pg_read_server_files to pg_write_all_data in finance_agent (database: finance_agent)
ALTER DEFAULT PRIVILEGES FOR ROLE "pg_read_server_files" GRANT ALL PRIVILEGES ON TABLES TO "pg_write_all_data";

-- grant future sequences from pg_read_server_files to pg_write_all_data in finance_agent (database: finance_agent)
ALTER DEFAULT PRIVILEGES FOR ROLE "pg_read_server_files" GRANT ALL PRIVILEGES ON SEQUENCES TO "pg_write_all_data";

-- grant future functions from pg_read_server_files to pg_write_all_data in finance_agent (database: finance_agent)
ALTER DEFAULT PRIVILEGES FOR ROLE "pg_read_server_files" GRANT ALL PRIVILEGES ON FUNCTIONS TO "pg_write_all_data";

-- grant future schemas from pg_read_server_files to pg_write_server_files in finance_agent (database: finance_agent)
ALTER DEFAULT PRIVILEGES FOR ROLE "pg_read_server_files" GRANT ALL PRIVILEGES ON SCHEMAS TO "pg_write_server_files";

-- grant future tables from pg_read_server_files to pg_write_server_files in finance_agent (database: finance_agent)
ALTER DEFAULT PRIVILEGES FOR ROLE "pg_read_server_files" GRANT ALL PRIVILEGES ON TABLES TO "pg_write_server_files";

-- grant future sequences from pg_read_server_files to pg_write_server_files in finance_agent (database: finance_agent)
ALTER DEFAULT PRIVILEGES FOR ROLE "pg_read_server_files" GRANT ALL PRIVILEGES ON SEQUENCES TO "pg_write_server_files";

-- grant future functions from pg_read_server_files to pg_write_server_files in finance_agent (database: finance_agent)
ALTER DEFAULT PRIVILEGES FOR ROLE "pg_read_server_files" GRANT ALL PRIVILEGES ON FUNCTIONS TO "pg_write_server_files";

-- grant future schemas from pg_read_server_files to postgres in finance_agent (database: finance_agent)
ALTER DEFAULT PRIVILEGES FOR ROLE "pg_read_server_files" GRANT ALL PRIVILEGES ON SCHEMAS TO "postgres";

-- grant future tables from pg_read_server_files to postgres in finance_agent (database: finance_agent)
ALTER DEFAULT PRIVILEGES FOR ROLE "pg_read_server_files" GRANT ALL PRIVILEGES ON TABLES TO "postgres";

-- grant future sequences from pg_read_server_files to postgres in finance_agent (database: finance_agent)
ALTER DEFAULT PRIVILEGES FOR ROLE "pg_read_server_files" GRANT ALL PRIVILEGES ON SEQUENCES TO "postgres";

-- grant future functions from pg_read_server_files to postgres in finance_agent (database: finance_agent)
ALTER DEFAULT PRIVILEGES FOR ROLE "pg_read_server_files" GRANT ALL PRIVILEGES ON FUNCTIONS TO "postgres";

-- grant future schemas from pg_signal_backend to pg_checkpoint in finance_agent (database: finance_agent)
ALTER DEFAULT PRIVILEGES FOR ROLE "pg_signal_backend" GRANT ALL PRIVILEGES ON SCHEMAS TO "pg_checkpoint";

-- grant future tables from pg_signal_backend to pg_checkpoint in finance_agent (database: finance_agent)
ALTER DEFAULT PRIVILEGES FOR ROLE "pg_signal_backend" GRANT ALL PRIVILEGES ON TABLES TO "pg_checkpoint";

-- grant future sequences from pg_signal_backend to pg_checkpoint in finance_agent (database: finance_agent)
ALTER DEFAULT PRIVILEGES FOR ROLE "pg_signal_backend" GRANT ALL PRIVILEGES ON SEQUENCES TO "pg_checkpoint";

-- grant future functions from pg_signal_backend to pg_checkpoint in finance_agent (database: finance_agent)
ALTER DEFAULT PRIVILEGES FOR ROLE "pg_signal_backend" GRANT ALL PRIVILEGES ON FUNCTIONS TO "pg_checkpoint";

-- grant future schemas from pg_signal_backend to pg_create_subscription in finance_agent (database: finance_agent)
ALTER DEFAULT PRIVILEGES FOR ROLE "pg_signal_backend" GRANT ALL PRIVILEGES ON SCHEMAS TO "pg_create_subscription";

-- grant future tables from pg_signal_backend to pg_create_subscription in finance_agent (database: finance_agent)
ALTER DEFAULT PRIVILEGES FOR ROLE "pg_signal_backend" GRANT ALL PRIVILEGES ON TABLES TO "pg_create_subscription";

-- grant future sequences from pg_signal_backend to pg_create_subscription in finance_agent (database: finance_agent)
ALTER DEFAULT PRIVILEGES FOR ROLE "pg_signal_backend" GRANT ALL PRIVILEGES ON SEQUENCES TO "pg_create_subscription";

-- grant future functions from pg_signal_backend to pg_create_subscription in finance_agent (database: finance_agent)
ALTER DEFAULT PRIVILEGES FOR ROLE "pg_signal_backend" GRANT ALL PRIVILEGES ON FUNCTIONS TO "pg_create_subscription";

-- grant future schemas from pg_signal_backend to pg_database_owner in finance_agent (database: finance_agent)
ALTER DEFAULT PRIVILEGES FOR ROLE "pg_signal_backend" GRANT ALL PRIVILEGES ON SCHEMAS TO "pg_database_owner";

-- grant future tables from pg_signal_backend to pg_database_owner in finance_agent (database: finance_agent)
ALTER DEFAULT PRIVILEGES FOR ROLE "pg_signal_backend" GRANT ALL PRIVILEGES ON TABLES TO "pg_database_owner";

-- grant future sequences from pg_signal_backend to pg_database_owner in finance_agent (database: finance_agent)
ALTER DEFAULT PRIVILEGES FOR ROLE "pg_signal_backend" GRANT ALL PRIVILEGES ON SEQUENCES TO "pg_database_owner";

-- grant future functions from pg_signal_backend to pg_database_owner in finance_agent (database: finance_agent)
ALTER DEFAULT PRIVILEGES FOR ROLE "pg_signal_backend" GRANT ALL PRIVILEGES ON FUNCTIONS TO "pg_database_owner";

-- grant future schemas from pg_signal_backend to pg_execute_server_program in finance_agent (database: finance_agent)
ALTER DEFAULT PRIVILEGES FOR ROLE "pg_signal_backend" GRANT ALL PRIVILEGES ON SCHEMAS TO "pg_execute_server_program";

-- grant future tables from pg_signal_backend to pg_execute_server_program in finance_agent (database: finance_agent)
ALTER DEFAULT PRIVILEGES FOR ROLE "pg_signal_backend" GRANT ALL PRIVILEGES ON TABLES TO "pg_execute_server_program";

-- grant future sequences from pg_signal_backend to pg_execute_server_program in finance_agent (database: finance_agent)
ALTER DEFAULT PRIVILEGES FOR ROLE "pg_signal_backend" GRANT ALL PRIVILEGES ON SEQUENCES TO "pg_execute_server_program";

-- grant future functions from pg_signal_backend to pg_execute_server_program in finance_agent (database: finance_agent)
ALTER DEFAULT PRIVILEGES FOR ROLE "pg_signal_backend" GRANT ALL PRIVILEGES ON FUNCTIONS TO "pg_execute_server_program";

-- grant future schemas from pg_signal_backend to pg_maintain in finance_agent (database: finance_agent)
ALTER DEFAULT PRIVILEGES FOR ROLE "pg_signal_backend" GRANT ALL PRIVILEGES ON SCHEMAS TO "pg_maintain";

-- grant future tables from pg_signal_backend to pg_maintain in finance_agent (database: finance_agent)
ALTER DEFAULT PRIVILEGES FOR ROLE "pg_signal_backend" GRANT ALL PRIVILEGES ON TABLES TO "pg_maintain";

-- grant future sequences from pg_signal_backend to pg_maintain in finance_agent (database: finance_agent)
ALTER DEFAULT PRIVILEGES FOR ROLE "pg_signal_backend" GRANT ALL PRIVILEGES ON SEQUENCES TO "pg_maintain";

-- grant future functions from pg_signal_backend to pg_maintain in finance_agent (database: finance_agent)
ALTER DEFAULT PRIVILEGES FOR ROLE "pg_signal_backend" GRANT ALL PRIVILEGES ON FUNCTIONS TO "pg_maintain";

-- grant future schemas from pg_signal_backend to pg_monitor in finance_agent (database: finance_agent)
ALTER DEFAULT PRIVILEGES FOR ROLE "pg_signal_backend" GRANT ALL PRIVILEGES ON SCHEMAS TO "pg_monitor";

-- grant future tables from pg_signal_backend to pg_monitor in finance_agent (database: finance_agent)
ALTER DEFAULT PRIVILEGES FOR ROLE "pg_signal_backend" GRANT ALL PRIVILEGES ON TABLES TO "pg_monitor";

-- grant future sequences from pg_signal_backend to pg_monitor in finance_agent (database: finance_agent)
ALTER DEFAULT PRIVILEGES FOR ROLE "pg_signal_backend" GRANT ALL PRIVILEGES ON SEQUENCES TO "pg_monitor";

-- grant future functions from pg_signal_backend to pg_monitor in finance_agent (database: finance_agent)
ALTER DEFAULT PRIVILEGES FOR ROLE "pg_signal_backend" GRANT ALL PRIVILEGES ON FUNCTIONS TO "pg_monitor";

-- grant future schemas from pg_signal_backend to pg_read_all_data in finance_agent (database: finance_agent)
ALTER DEFAULT PRIVILEGES FOR ROLE "pg_signal_backend" GRANT ALL PRIVILEGES ON SCHEMAS TO "pg_read_all_data";

-- grant future tables from pg_signal_backend to pg_read_all_data in finance_agent (database: finance_agent)
ALTER DEFAULT PRIVILEGES FOR ROLE "pg_signal_backend" GRANT ALL PRIVILEGES ON TABLES TO "pg_read_all_data";

-- grant future sequences from pg_signal_backend to pg_read_all_data in finance_agent (database: finance_agent)
ALTER DEFAULT PRIVILEGES FOR ROLE "pg_signal_backend" GRANT ALL PRIVILEGES ON SEQUENCES TO "pg_read_all_data";

-- grant future functions from pg_signal_backend to pg_read_all_data in finance_agent (database: finance_agent)
ALTER DEFAULT PRIVILEGES FOR ROLE "pg_signal_backend" GRANT ALL PRIVILEGES ON FUNCTIONS TO "pg_read_all_data";

-- grant future schemas from pg_signal_backend to pg_read_all_stats in finance_agent (database: finance_agent)
ALTER DEFAULT PRIVILEGES FOR ROLE "pg_signal_backend" GRANT ALL PRIVILEGES ON SCHEMAS TO "pg_read_all_stats";

-- grant future tables from pg_signal_backend to pg_read_all_stats in finance_agent (database: finance_agent)
ALTER DEFAULT PRIVILEGES FOR ROLE "pg_signal_backend" GRANT ALL PRIVILEGES ON TABLES TO "pg_read_all_stats";

-- grant future sequences from pg_signal_backend to pg_read_all_stats in finance_agent (database: finance_agent)
ALTER DEFAULT PRIVILEGES FOR ROLE "pg_signal_backend" GRANT ALL PRIVILEGES ON SEQUENCES TO "pg_read_all_stats";

-- grant future functions from pg_signal_backend to pg_read_all_stats in finance_agent (database: finance_agent)
ALTER DEFAULT PRIVILEGES FOR ROLE "pg_signal_backend" GRANT ALL PRIVILEGES ON FUNCTIONS TO "pg_read_all_stats";

-- grant future schemas from pg_signal_backend to pg_read_all_settings in finance_agent (database: finance_agent)
ALTER DEFAULT PRIVILEGES FOR ROLE "pg_signal_backend" GRANT ALL PRIVILEGES ON SCHEMAS TO "pg_read_all_settings";

-- grant future tables from pg_signal_backend to pg_read_all_settings in finance_agent (database: finance_agent)
ALTER DEFAULT PRIVILEGES FOR ROLE "pg_signal_backend" GRANT ALL PRIVILEGES ON TABLES TO "pg_read_all_settings";

-- grant future sequences from pg_signal_backend to pg_read_all_settings in finance_agent (database: finance_agent)
ALTER DEFAULT PRIVILEGES FOR ROLE "pg_signal_backend" GRANT ALL PRIVILEGES ON SEQUENCES TO "pg_read_all_settings";

-- grant future functions from pg_signal_backend to pg_read_all_settings in finance_agent (database: finance_agent)
ALTER DEFAULT PRIVILEGES FOR ROLE "pg_signal_backend" GRANT ALL PRIVILEGES ON FUNCTIONS TO "pg_read_all_settings";

-- grant future schemas from pg_signal_backend to pg_read_server_files in finance_agent (database: finance_agent)
ALTER DEFAULT PRIVILEGES FOR ROLE "pg_signal_backend" GRANT ALL PRIVILEGES ON SCHEMAS TO "pg_read_server_files";

-- grant future tables from pg_signal_backend to pg_read_server_files in finance_agent (database: finance_agent)
ALTER DEFAULT PRIVILEGES FOR ROLE "pg_signal_backend" GRANT ALL PRIVILEGES ON TABLES TO "pg_read_server_files";

-- grant future sequences from pg_signal_backend to pg_read_server_files in finance_agent (database: finance_agent)
ALTER DEFAULT PRIVILEGES FOR ROLE "pg_signal_backend" GRANT ALL PRIVILEGES ON SEQUENCES TO "pg_read_server_files";

-- grant future functions from pg_signal_backend to pg_read_server_files in finance_agent (database: finance_agent)
ALTER DEFAULT PRIVILEGES FOR ROLE "pg_signal_backend" GRANT ALL PRIVILEGES ON FUNCTIONS TO "pg_read_server_files";

-- grant future schemas from pg_signal_backend to pg_stat_scan_tables in finance_agent (database: finance_agent)
ALTER DEFAULT PRIVILEGES FOR ROLE "pg_signal_backend" GRANT ALL PRIVILEGES ON SCHEMAS TO "pg_stat_scan_tables";

-- grant future tables from pg_signal_backend to pg_stat_scan_tables in finance_agent (database: finance_agent)
ALTER DEFAULT PRIVILEGES FOR ROLE "pg_signal_backend" GRANT ALL PRIVILEGES ON TABLES TO "pg_stat_scan_tables";

-- grant future sequences from pg_signal_backend to pg_stat_scan_tables in finance_agent (database: finance_agent)
ALTER DEFAULT PRIVILEGES FOR ROLE "pg_signal_backend" GRANT ALL PRIVILEGES ON SEQUENCES TO "pg_stat_scan_tables";

-- grant future functions from pg_signal_backend to pg_stat_scan_tables in finance_agent (database: finance_agent)
ALTER DEFAULT PRIVILEGES FOR ROLE "pg_signal_backend" GRANT ALL PRIVILEGES ON FUNCTIONS TO "pg_stat_scan_tables";

-- grant future schemas from pg_signal_backend to pg_use_reserved_connections in finance_agent (database: finance_agent)
ALTER DEFAULT PRIVILEGES FOR ROLE "pg_signal_backend" GRANT ALL PRIVILEGES ON SCHEMAS TO "pg_use_reserved_connections";

-- grant future tables from pg_signal_backend to pg_use_reserved_connections in finance_agent (database: finance_agent)
ALTER DEFAULT PRIVILEGES FOR ROLE "pg_signal_backend" GRANT ALL PRIVILEGES ON TABLES TO "pg_use_reserved_connections";

-- grant future sequences from pg_signal_backend to pg_use_reserved_connections in finance_agent (database: finance_agent)
ALTER DEFAULT PRIVILEGES FOR ROLE "pg_signal_backend" GRANT ALL PRIVILEGES ON SEQUENCES TO "pg_use_reserved_connections";

-- grant future functions from pg_signal_backend to pg_use_reserved_connections in finance_agent (database: finance_agent)
ALTER DEFAULT PRIVILEGES FOR ROLE "pg_signal_backend" GRANT ALL PRIVILEGES ON FUNCTIONS TO "pg_use_reserved_connections";

-- grant future schemas from pg_signal_backend to pg_write_all_data in finance_agent (database: finance_agent)
ALTER DEFAULT PRIVILEGES FOR ROLE "pg_signal_backend" GRANT ALL PRIVILEGES ON SCHEMAS TO "pg_write_all_data";

-- grant future tables from pg_signal_backend to pg_write_all_data in finance_agent (database: finance_agent)
ALTER DEFAULT PRIVILEGES FOR ROLE "pg_signal_backend" GRANT ALL PRIVILEGES ON TABLES TO "pg_write_all_data";

-- grant future sequences from pg_signal_backend to pg_write_all_data in finance_agent (database: finance_agent)
ALTER DEFAULT PRIVILEGES FOR ROLE "pg_signal_backend" GRANT ALL PRIVILEGES ON SEQUENCES TO "pg_write_all_data";

-- grant future functions from pg_signal_backend to pg_write_all_data in finance_agent (database: finance_agent)
ALTER DEFAULT PRIVILEGES FOR ROLE "pg_signal_backend" GRANT ALL PRIVILEGES ON FUNCTIONS TO "pg_write_all_data";

-- grant future schemas from pg_signal_backend to pg_write_server_files in finance_agent (database: finance_agent)
ALTER DEFAULT PRIVILEGES FOR ROLE "pg_signal_backend" GRANT ALL PRIVILEGES ON SCHEMAS TO "pg_write_server_files";

-- grant future tables from pg_signal_backend to pg_write_server_files in finance_agent (database: finance_agent)
ALTER DEFAULT PRIVILEGES FOR ROLE "pg_signal_backend" GRANT ALL PRIVILEGES ON TABLES TO "pg_write_server_files";

-- grant future sequences from pg_signal_backend to pg_write_server_files in finance_agent (database: finance_agent)
ALTER DEFAULT PRIVILEGES FOR ROLE "pg_signal_backend" GRANT ALL PRIVILEGES ON SEQUENCES TO "pg_write_server_files";

-- grant future functions from pg_signal_backend to pg_write_server_files in finance_agent (database: finance_agent)
ALTER DEFAULT PRIVILEGES FOR ROLE "pg_signal_backend" GRANT ALL PRIVILEGES ON FUNCTIONS TO "pg_write_server_files";

-- grant future schemas from pg_signal_backend to postgres in finance_agent (database: finance_agent)
ALTER DEFAULT PRIVILEGES FOR ROLE "pg_signal_backend" GRANT ALL PRIVILEGES ON SCHEMAS TO "postgres";

-- grant future tables from pg_signal_backend to postgres in finance_agent (database: finance_agent)
ALTER DEFAULT PRIVILEGES FOR ROLE "pg_signal_backend" GRANT ALL PRIVILEGES ON TABLES TO "postgres";

-- grant future sequences from pg_signal_backend to postgres in finance_agent (database: finance_agent)
ALTER DEFAULT PRIVILEGES FOR ROLE "pg_signal_backend" GRANT ALL PRIVILEGES ON SEQUENCES TO "postgres";

-- grant future functions from pg_signal_backend to postgres in finance_agent (database: finance_agent)
ALTER DEFAULT PRIVILEGES FOR ROLE "pg_signal_backend" GRANT ALL PRIVILEGES ON FUNCTIONS TO "postgres";

-- grant future schemas from pg_stat_scan_tables to pg_checkpoint in finance_agent (database: finance_agent)
ALTER DEFAULT PRIVILEGES FOR ROLE "pg_stat_scan_tables" GRANT ALL PRIVILEGES ON SCHEMAS TO "pg_checkpoint";

-- grant future tables from pg_stat_scan_tables to pg_checkpoint in finance_agent (database: finance_agent)
ALTER DEFAULT PRIVILEGES FOR ROLE "pg_stat_scan_tables" GRANT ALL PRIVILEGES ON TABLES TO "pg_checkpoint";

-- grant future sequences from pg_stat_scan_tables to pg_checkpoint in finance_agent (database: finance_agent)
ALTER DEFAULT PRIVILEGES FOR ROLE "pg_stat_scan_tables" GRANT ALL PRIVILEGES ON SEQUENCES TO "pg_checkpoint";

-- grant future functions from pg_stat_scan_tables to pg_checkpoint in finance_agent (database: finance_agent)
ALTER DEFAULT PRIVILEGES FOR ROLE "pg_stat_scan_tables" GRANT ALL PRIVILEGES ON FUNCTIONS TO "pg_checkpoint";

-- grant future schemas from pg_stat_scan_tables to pg_create_subscription in finance_agent (database: finance_agent)
ALTER DEFAULT PRIVILEGES FOR ROLE "pg_stat_scan_tables" GRANT ALL PRIVILEGES ON SCHEMAS TO "pg_create_subscription";

-- grant future tables from pg_stat_scan_tables to pg_create_subscription in finance_agent (database: finance_agent)
ALTER DEFAULT PRIVILEGES FOR ROLE "pg_stat_scan_tables" GRANT ALL PRIVILEGES ON TABLES TO "pg_create_subscription";

-- grant future sequences from pg_stat_scan_tables to pg_create_subscription in finance_agent (database: finance_agent)
ALTER DEFAULT PRIVILEGES FOR ROLE "pg_stat_scan_tables" GRANT ALL PRIVILEGES ON SEQUENCES TO "pg_create_subscription";

-- grant future functions from pg_stat_scan_tables to pg_create_subscription in finance_agent (database: finance_agent)
ALTER DEFAULT PRIVILEGES FOR ROLE "pg_stat_scan_tables" GRANT ALL PRIVILEGES ON FUNCTIONS TO "pg_create_subscription";

-- grant future schemas from pg_stat_scan_tables to pg_database_owner in finance_agent (database: finance_agent)
ALTER DEFAULT PRIVILEGES FOR ROLE "pg_stat_scan_tables" GRANT ALL PRIVILEGES ON SCHEMAS TO "pg_database_owner";

-- grant future tables from pg_stat_scan_tables to pg_database_owner in finance_agent (database: finance_agent)
ALTER DEFAULT PRIVILEGES FOR ROLE "pg_stat_scan_tables" GRANT ALL PRIVILEGES ON TABLES TO "pg_database_owner";

-- grant future sequences from pg_stat_scan_tables to pg_database_owner in finance_agent (database: finance_agent)
ALTER DEFAULT PRIVILEGES FOR ROLE "pg_stat_scan_tables" GRANT ALL PRIVILEGES ON SEQUENCES TO "pg_database_owner";

-- grant future functions from pg_stat_scan_tables to pg_database_owner in finance_agent (database: finance_agent)
ALTER DEFAULT PRIVILEGES FOR ROLE "pg_stat_scan_tables" GRANT ALL PRIVILEGES ON FUNCTIONS TO "pg_database_owner";

-- grant future schemas from pg_stat_scan_tables to pg_execute_server_program in finance_agent (database: finance_agent)
ALTER DEFAULT PRIVILEGES FOR ROLE "pg_stat_scan_tables" GRANT ALL PRIVILEGES ON SCHEMAS TO "pg_execute_server_program";

-- grant future tables from pg_stat_scan_tables to pg_execute_server_program in finance_agent (database: finance_agent)
ALTER DEFAULT PRIVILEGES FOR ROLE "pg_stat_scan_tables" GRANT ALL PRIVILEGES ON TABLES TO "pg_execute_server_program";

-- grant future sequences from pg_stat_scan_tables to pg_execute_server_program in finance_agent (database: finance_agent)
ALTER DEFAULT PRIVILEGES FOR ROLE "pg_stat_scan_tables" GRANT ALL PRIVILEGES ON SEQUENCES TO "pg_execute_server_program";

-- grant future functions from pg_stat_scan_tables to pg_execute_server_program in finance_agent (database: finance_agent)
ALTER DEFAULT PRIVILEGES FOR ROLE "pg_stat_scan_tables" GRANT ALL PRIVILEGES ON FUNCTIONS TO "pg_execute_server_program";

-- grant future schemas from pg_stat_scan_tables to pg_maintain in finance_agent (database: finance_agent)
ALTER DEFAULT PRIVILEGES FOR ROLE "pg_stat_scan_tables" GRANT ALL PRIVILEGES ON SCHEMAS TO "pg_maintain";

-- grant future tables from pg_stat_scan_tables to pg_maintain in finance_agent (database: finance_agent)
ALTER DEFAULT PRIVILEGES FOR ROLE "pg_stat_scan_tables" GRANT ALL PRIVILEGES ON TABLES TO "pg_maintain";

-- grant future sequences from pg_stat_scan_tables to pg_maintain in finance_agent (database: finance_agent)
ALTER DEFAULT PRIVILEGES FOR ROLE "pg_stat_scan_tables" GRANT ALL PRIVILEGES ON SEQUENCES TO "pg_maintain";

-- grant future functions from pg_stat_scan_tables to pg_maintain in finance_agent (database: finance_agent)
ALTER DEFAULT PRIVILEGES FOR ROLE "pg_stat_scan_tables" GRANT ALL PRIVILEGES ON FUNCTIONS TO "pg_maintain";

-- grant future schemas from pg_stat_scan_tables to pg_monitor in finance_agent (database: finance_agent)
ALTER DEFAULT PRIVILEGES FOR ROLE "pg_stat_scan_tables" GRANT ALL PRIVILEGES ON SCHEMAS TO "pg_monitor";

-- grant future tables from pg_stat_scan_tables to pg_monitor in finance_agent (database: finance_agent)
ALTER DEFAULT PRIVILEGES FOR ROLE "pg_stat_scan_tables" GRANT ALL PRIVILEGES ON TABLES TO "pg_monitor";

-- grant future sequences from pg_stat_scan_tables to pg_monitor in finance_agent (database: finance_agent)
ALTER DEFAULT PRIVILEGES FOR ROLE "pg_stat_scan_tables" GRANT ALL PRIVILEGES ON SEQUENCES TO "pg_monitor";

-- grant future functions from pg_stat_scan_tables to pg_monitor in finance_agent (database: finance_agent)
ALTER DEFAULT PRIVILEGES FOR ROLE "pg_stat_scan_tables" GRANT ALL PRIVILEGES ON FUNCTIONS TO "pg_monitor";

-- grant future schemas from pg_stat_scan_tables to pg_read_all_data in finance_agent (database: finance_agent)
ALTER DEFAULT PRIVILEGES FOR ROLE "pg_stat_scan_tables" GRANT ALL PRIVILEGES ON SCHEMAS TO "pg_read_all_data";

-- grant future tables from pg_stat_scan_tables to pg_read_all_data in finance_agent (database: finance_agent)
ALTER DEFAULT PRIVILEGES FOR ROLE "pg_stat_scan_tables" GRANT ALL PRIVILEGES ON TABLES TO "pg_read_all_data";

-- grant future sequences from pg_stat_scan_tables to pg_read_all_data in finance_agent (database: finance_agent)
ALTER DEFAULT PRIVILEGES FOR ROLE "pg_stat_scan_tables" GRANT ALL PRIVILEGES ON SEQUENCES TO "pg_read_all_data";

-- grant future functions from pg_stat_scan_tables to pg_read_all_data in finance_agent (database: finance_agent)
ALTER DEFAULT PRIVILEGES FOR ROLE "pg_stat_scan_tables" GRANT ALL PRIVILEGES ON FUNCTIONS TO "pg_read_all_data";

-- grant future schemas from pg_stat_scan_tables to pg_read_all_stats in finance_agent (database: finance_agent)
ALTER DEFAULT PRIVILEGES FOR ROLE "pg_stat_scan_tables" GRANT ALL PRIVILEGES ON SCHEMAS TO "pg_read_all_stats";

-- grant future tables from pg_stat_scan_tables to pg_read_all_stats in finance_agent (database: finance_agent)
ALTER DEFAULT PRIVILEGES FOR ROLE "pg_stat_scan_tables" GRANT ALL PRIVILEGES ON TABLES TO "pg_read_all_stats";

-- grant future sequences from pg_stat_scan_tables to pg_read_all_stats in finance_agent (database: finance_agent)
ALTER DEFAULT PRIVILEGES FOR ROLE "pg_stat_scan_tables" GRANT ALL PRIVILEGES ON SEQUENCES TO "pg_read_all_stats";

-- grant future functions from pg_stat_scan_tables to pg_read_all_stats in finance_agent (database: finance_agent)
ALTER DEFAULT PRIVILEGES FOR ROLE "pg_stat_scan_tables" GRANT ALL PRIVILEGES ON FUNCTIONS TO "pg_read_all_stats";

-- grant future schemas from pg_stat_scan_tables to pg_read_all_settings in finance_agent (database: finance_agent)
ALTER DEFAULT PRIVILEGES FOR ROLE "pg_stat_scan_tables" GRANT ALL PRIVILEGES ON SCHEMAS TO "pg_read_all_settings";

-- grant future tables from pg_stat_scan_tables to pg_read_all_settings in finance_agent (database: finance_agent)
ALTER DEFAULT PRIVILEGES FOR ROLE "pg_stat_scan_tables" GRANT ALL PRIVILEGES ON TABLES TO "pg_read_all_settings";

-- grant future sequences from pg_stat_scan_tables to pg_read_all_settings in finance_agent (database: finance_agent)
ALTER DEFAULT PRIVILEGES FOR ROLE "pg_stat_scan_tables" GRANT ALL PRIVILEGES ON SEQUENCES TO "pg_read_all_settings";

-- grant future functions from pg_stat_scan_tables to pg_read_all_settings in finance_agent (database: finance_agent)
ALTER DEFAULT PRIVILEGES FOR ROLE "pg_stat_scan_tables" GRANT ALL PRIVILEGES ON FUNCTIONS TO "pg_read_all_settings";

-- grant future schemas from pg_stat_scan_tables to pg_read_server_files in finance_agent (database: finance_agent)
ALTER DEFAULT PRIVILEGES FOR ROLE "pg_stat_scan_tables" GRANT ALL PRIVILEGES ON SCHEMAS TO "pg_read_server_files";

-- grant future tables from pg_stat_scan_tables to pg_read_server_files in finance_agent (database: finance_agent)
ALTER DEFAULT PRIVILEGES FOR ROLE "pg_stat_scan_tables" GRANT ALL PRIVILEGES ON TABLES TO "pg_read_server_files";

-- grant future sequences from pg_stat_scan_tables to pg_read_server_files in finance_agent (database: finance_agent)
ALTER DEFAULT PRIVILEGES FOR ROLE "pg_stat_scan_tables" GRANT ALL PRIVILEGES ON SEQUENCES TO "pg_read_server_files";

-- grant future functions from pg_stat_scan_tables to pg_read_server_files in finance_agent (database: finance_agent)
ALTER DEFAULT PRIVILEGES FOR ROLE "pg_stat_scan_tables" GRANT ALL PRIVILEGES ON FUNCTIONS TO "pg_read_server_files";

-- grant future schemas from pg_stat_scan_tables to pg_signal_backend in finance_agent (database: finance_agent)
ALTER DEFAULT PRIVILEGES FOR ROLE "pg_stat_scan_tables" GRANT ALL PRIVILEGES ON SCHEMAS TO "pg_signal_backend";

-- grant future tables from pg_stat_scan_tables to pg_signal_backend in finance_agent (database: finance_agent)
ALTER DEFAULT PRIVILEGES FOR ROLE "pg_stat_scan_tables" GRANT ALL PRIVILEGES ON TABLES TO "pg_signal_backend";

-- grant future sequences from pg_stat_scan_tables to pg_signal_backend in finance_agent (database: finance_agent)
ALTER DEFAULT PRIVILEGES FOR ROLE "pg_stat_scan_tables" GRANT ALL PRIVILEGES ON SEQUENCES TO "pg_signal_backend";

-- grant future functions from pg_stat_scan_tables to pg_signal_backend in finance_agent (database: finance_agent)
ALTER DEFAULT PRIVILEGES FOR ROLE "pg_stat_scan_tables" GRANT ALL PRIVILEGES ON FUNCTIONS TO "pg_signal_backend";

-- grant future schemas from pg_stat_scan_tables to pg_use_reserved_connections in finance_agent (database: finance_agent)
ALTER DEFAULT PRIVILEGES FOR ROLE "pg_stat_scan_tables" GRANT ALL PRIVILEGES ON SCHEMAS TO "pg_use_reserved_connections";

-- grant future tables from pg_stat_scan_tables to pg_use_reserved_connections in finance_agent (database: finance_agent)
ALTER DEFAULT PRIVILEGES FOR ROLE "pg_stat_scan_tables" GRANT ALL PRIVILEGES ON TABLES TO "pg_use_reserved_connections";

-- grant future sequences from pg_stat_scan_tables to pg_use_reserved_connections in finance_agent (database: finance_agent)
ALTER DEFAULT PRIVILEGES FOR ROLE "pg_stat_scan_tables" GRANT ALL PRIVILEGES ON SEQUENCES TO "pg_use_reserved_connections";

-- grant future functions from pg_stat_scan_tables to pg_use_reserved_connections in finance_agent (database: finance_agent)
ALTER DEFAULT PRIVILEGES FOR ROLE "pg_stat_scan_tables" GRANT ALL PRIVILEGES ON FUNCTIONS TO "pg_use_reserved_connections";

-- grant future schemas from pg_stat_scan_tables to pg_write_all_data in finance_agent (database: finance_agent)
ALTER DEFAULT PRIVILEGES FOR ROLE "pg_stat_scan_tables" GRANT ALL PRIVILEGES ON SCHEMAS TO "pg_write_all_data";

-- grant future tables from pg_stat_scan_tables to pg_write_all_data in finance_agent (database: finance_agent)
ALTER DEFAULT PRIVILEGES FOR ROLE "pg_stat_scan_tables" GRANT ALL PRIVILEGES ON TABLES TO "pg_write_all_data";

-- grant future sequences from pg_stat_scan_tables to pg_write_all_data in finance_agent (database: finance_agent)
ALTER DEFAULT PRIVILEGES FOR ROLE "pg_stat_scan_tables" GRANT ALL PRIVILEGES ON SEQUENCES TO "pg_write_all_data";

-- grant future functions from pg_stat_scan_tables to pg_write_all_data in finance_agent (database: finance_agent)
ALTER DEFAULT PRIVILEGES FOR ROLE "pg_stat_scan_tables" GRANT ALL PRIVILEGES ON FUNCTIONS TO "pg_write_all_data";

-- grant future schemas from pg_stat_scan_tables to pg_write_server_files in finance_agent (database: finance_agent)
ALTER DEFAULT PRIVILEGES FOR ROLE "pg_stat_scan_tables" GRANT ALL PRIVILEGES ON SCHEMAS TO "pg_write_server_files";

-- grant future tables from pg_stat_scan_tables to pg_write_server_files in finance_agent (database: finance_agent)
ALTER DEFAULT PRIVILEGES FOR ROLE "pg_stat_scan_tables" GRANT ALL PRIVILEGES ON TABLES TO "pg_write_server_files";

-- grant future sequences from pg_stat_scan_tables to pg_write_server_files in finance_agent (database: finance_agent)
ALTER DEFAULT PRIVILEGES FOR ROLE "pg_stat_scan_tables" GRANT ALL PRIVILEGES ON SEQUENCES TO "pg_write_server_files";

-- grant future functions from pg_stat_scan_tables to pg_write_server_files in finance_agent (database: finance_agent)
ALTER DEFAULT PRIVILEGES FOR ROLE "pg_stat_scan_tables" GRANT ALL PRIVILEGES ON FUNCTIONS TO "pg_write_server_files";

-- grant future schemas from pg_stat_scan_tables to postgres in finance_agent (database: finance_agent)
ALTER DEFAULT PRIVILEGES FOR ROLE "pg_stat_scan_tables" GRANT ALL PRIVILEGES ON SCHEMAS TO "postgres";

-- grant future tables from pg_stat_scan_tables to postgres in finance_agent (database: finance_agent)
ALTER DEFAULT PRIVILEGES FOR ROLE "pg_stat_scan_tables" GRANT ALL PRIVILEGES ON TABLES TO "postgres";

-- grant future sequences from pg_stat_scan_tables to postgres in finance_agent (database: finance_agent)
ALTER DEFAULT PRIVILEGES FOR ROLE "pg_stat_scan_tables" GRANT ALL PRIVILEGES ON SEQUENCES TO "postgres";

-- grant future functions from pg_stat_scan_tables to postgres in finance_agent (database: finance_agent)
ALTER DEFAULT PRIVILEGES FOR ROLE "pg_stat_scan_tables" GRANT ALL PRIVILEGES ON FUNCTIONS TO "postgres";

-- grant future schemas from pg_use_reserved_connections to pg_checkpoint in finance_agent (database: finance_agent)
ALTER DEFAULT PRIVILEGES FOR ROLE "pg_use_reserved_connections" GRANT ALL PRIVILEGES ON SCHEMAS TO "pg_checkpoint";

-- grant future tables from pg_use_reserved_connections to pg_checkpoint in finance_agent (database: finance_agent)
ALTER DEFAULT PRIVILEGES FOR ROLE "pg_use_reserved_connections" GRANT ALL PRIVILEGES ON TABLES TO "pg_checkpoint";

-- grant future sequences from pg_use_reserved_connections to pg_checkpoint in finance_agent (database: finance_agent)
ALTER DEFAULT PRIVILEGES FOR ROLE "pg_use_reserved_connections" GRANT ALL PRIVILEGES ON SEQUENCES TO "pg_checkpoint";

-- grant future functions from pg_use_reserved_connections to pg_checkpoint in finance_agent (database: finance_agent)
ALTER DEFAULT PRIVILEGES FOR ROLE "pg_use_reserved_connections" GRANT ALL PRIVILEGES ON FUNCTIONS TO "pg_checkpoint";

-- grant future schemas from pg_use_reserved_connections to pg_create_subscription in finance_agent (database: finance_agent)
ALTER DEFAULT PRIVILEGES FOR ROLE "pg_use_reserved_connections" GRANT ALL PRIVILEGES ON SCHEMAS TO "pg_create_subscription";

-- grant future tables from pg_use_reserved_connections to pg_create_subscription in finance_agent (database: finance_agent)
ALTER DEFAULT PRIVILEGES FOR ROLE "pg_use_reserved_connections" GRANT ALL PRIVILEGES ON TABLES TO "pg_create_subscription";

-- grant future sequences from pg_use_reserved_connections to pg_create_subscription in finance_agent (database: finance_agent)
ALTER DEFAULT PRIVILEGES FOR ROLE "pg_use_reserved_connections" GRANT ALL PRIVILEGES ON SEQUENCES TO "pg_create_subscription";

-- grant future functions from pg_use_reserved_connections to pg_create_subscription in finance_agent (database: finance_agent)
ALTER DEFAULT PRIVILEGES FOR ROLE "pg_use_reserved_connections" GRANT ALL PRIVILEGES ON FUNCTIONS TO "pg_create_subscription";

-- grant future schemas from pg_use_reserved_connections to pg_database_owner in finance_agent (database: finance_agent)
ALTER DEFAULT PRIVILEGES FOR ROLE "pg_use_reserved_connections" GRANT ALL PRIVILEGES ON SCHEMAS TO "pg_database_owner";

-- grant future tables from pg_use_reserved_connections to pg_database_owner in finance_agent (database: finance_agent)
ALTER DEFAULT PRIVILEGES FOR ROLE "pg_use_reserved_connections" GRANT ALL PRIVILEGES ON TABLES TO "pg_database_owner";

-- grant future sequences from pg_use_reserved_connections to pg_database_owner in finance_agent (database: finance_agent)
ALTER DEFAULT PRIVILEGES FOR ROLE "pg_use_reserved_connections" GRANT ALL PRIVILEGES ON SEQUENCES TO "pg_database_owner";

-- grant future functions from pg_use_reserved_connections to pg_database_owner in finance_agent (database: finance_agent)
ALTER DEFAULT PRIVILEGES FOR ROLE "pg_use_reserved_connections" GRANT ALL PRIVILEGES ON FUNCTIONS TO "pg_database_owner";

-- grant future schemas from pg_use_reserved_connections to pg_execute_server_program in finance_agent (database: finance_agent)
ALTER DEFAULT PRIVILEGES FOR ROLE "pg_use_reserved_connections" GRANT ALL PRIVILEGES ON SCHEMAS TO "pg_execute_server_program";

-- grant future tables from pg_use_reserved_connections to pg_execute_server_program in finance_agent (database: finance_agent)
ALTER DEFAULT PRIVILEGES FOR ROLE "pg_use_reserved_connections" GRANT ALL PRIVILEGES ON TABLES TO "pg_execute_server_program";

-- grant future sequences from pg_use_reserved_connections to pg_execute_server_program in finance_agent (database: finance_agent)
ALTER DEFAULT PRIVILEGES FOR ROLE "pg_use_reserved_connections" GRANT ALL PRIVILEGES ON SEQUENCES TO "pg_execute_server_program";

-- grant future functions from pg_use_reserved_connections to pg_execute_server_program in finance_agent (database: finance_agent)
ALTER DEFAULT PRIVILEGES FOR ROLE "pg_use_reserved_connections" GRANT ALL PRIVILEGES ON FUNCTIONS TO "pg_execute_server_program";

-- grant future schemas from pg_use_reserved_connections to pg_maintain in finance_agent (database: finance_agent)
ALTER DEFAULT PRIVILEGES FOR ROLE "pg_use_reserved_connections" GRANT ALL PRIVILEGES ON SCHEMAS TO "pg_maintain";

-- grant future tables from pg_use_reserved_connections to pg_maintain in finance_agent (database: finance_agent)
ALTER DEFAULT PRIVILEGES FOR ROLE "pg_use_reserved_connections" GRANT ALL PRIVILEGES ON TABLES TO "pg_maintain";

-- grant future sequences from pg_use_reserved_connections to pg_maintain in finance_agent (database: finance_agent)
ALTER DEFAULT PRIVILEGES FOR ROLE "pg_use_reserved_connections" GRANT ALL PRIVILEGES ON SEQUENCES TO "pg_maintain";

-- grant future functions from pg_use_reserved_connections to pg_maintain in finance_agent (database: finance_agent)
ALTER DEFAULT PRIVILEGES FOR ROLE "pg_use_reserved_connections" GRANT ALL PRIVILEGES ON FUNCTIONS TO "pg_maintain";

-- grant future schemas from pg_use_reserved_connections to pg_monitor in finance_agent (database: finance_agent)
ALTER DEFAULT PRIVILEGES FOR ROLE "pg_use_reserved_connections" GRANT ALL PRIVILEGES ON SCHEMAS TO "pg_monitor";

-- grant future tables from pg_use_reserved_connections to pg_monitor in finance_agent (database: finance_agent)
ALTER DEFAULT PRIVILEGES FOR ROLE "pg_use_reserved_connections" GRANT ALL PRIVILEGES ON TABLES TO "pg_monitor";

-- grant future sequences from pg_use_reserved_connections to pg_monitor in finance_agent (database: finance_agent)
ALTER DEFAULT PRIVILEGES FOR ROLE "pg_use_reserved_connections" GRANT ALL PRIVILEGES ON SEQUENCES TO "pg_monitor";

-- grant future functions from pg_use_reserved_connections to pg_monitor in finance_agent (database: finance_agent)
ALTER DEFAULT PRIVILEGES FOR ROLE "pg_use_reserved_connections" GRANT ALL PRIVILEGES ON FUNCTIONS TO "pg_monitor";

-- grant future schemas from pg_use_reserved_connections to pg_read_all_data in finance_agent (database: finance_agent)
ALTER DEFAULT PRIVILEGES FOR ROLE "pg_use_reserved_connections" GRANT ALL PRIVILEGES ON SCHEMAS TO "pg_read_all_data";

-- grant future tables from pg_use_reserved_connections to pg_read_all_data in finance_agent (database: finance_agent)
ALTER DEFAULT PRIVILEGES FOR ROLE "pg_use_reserved_connections" GRANT ALL PRIVILEGES ON TABLES TO "pg_read_all_data";

-- grant future sequences from pg_use_reserved_connections to pg_read_all_data in finance_agent (database: finance_agent)
ALTER DEFAULT PRIVILEGES FOR ROLE "pg_use_reserved_connections" GRANT ALL PRIVILEGES ON SEQUENCES TO "pg_read_all_data";

-- grant future functions from pg_use_reserved_connections to pg_read_all_data in finance_agent (database: finance_agent)
ALTER DEFAULT PRIVILEGES FOR ROLE "pg_use_reserved_connections" GRANT ALL PRIVILEGES ON FUNCTIONS TO "pg_read_all_data";

-- grant future schemas from pg_use_reserved_connections to pg_read_all_stats in finance_agent (database: finance_agent)
ALTER DEFAULT PRIVILEGES FOR ROLE "pg_use_reserved_connections" GRANT ALL PRIVILEGES ON SCHEMAS TO "pg_read_all_stats";

-- grant future tables from pg_use_reserved_connections to pg_read_all_stats in finance_agent (database: finance_agent)
ALTER DEFAULT PRIVILEGES FOR ROLE "pg_use_reserved_connections" GRANT ALL PRIVILEGES ON TABLES TO "pg_read_all_stats";

-- grant future sequences from pg_use_reserved_connections to pg_read_all_stats in finance_agent (database: finance_agent)
ALTER DEFAULT PRIVILEGES FOR ROLE "pg_use_reserved_connections" GRANT ALL PRIVILEGES ON SEQUENCES TO "pg_read_all_stats";

-- grant future functions from pg_use_reserved_connections to pg_read_all_stats in finance_agent (database: finance_agent)
ALTER DEFAULT PRIVILEGES FOR ROLE "pg_use_reserved_connections" GRANT ALL PRIVILEGES ON FUNCTIONS TO "pg_read_all_stats";

-- grant future schemas from pg_use_reserved_connections to pg_read_all_settings in finance_agent (database: finance_agent)
ALTER DEFAULT PRIVILEGES FOR ROLE "pg_use_reserved_connections" GRANT ALL PRIVILEGES ON SCHEMAS TO "pg_read_all_settings";

-- grant future tables from pg_use_reserved_connections to pg_read_all_settings in finance_agent (database: finance_agent)
ALTER DEFAULT PRIVILEGES FOR ROLE "pg_use_reserved_connections" GRANT ALL PRIVILEGES ON TABLES TO "pg_read_all_settings";

-- grant future sequences from pg_use_reserved_connections to pg_read_all_settings in finance_agent (database: finance_agent)
ALTER DEFAULT PRIVILEGES FOR ROLE "pg_use_reserved_connections" GRANT ALL PRIVILEGES ON SEQUENCES TO "pg_read_all_settings";

-- grant future functions from pg_use_reserved_connections to pg_read_all_settings in finance_agent (database: finance_agent)
ALTER DEFAULT PRIVILEGES FOR ROLE "pg_use_reserved_connections" GRANT ALL PRIVILEGES ON FUNCTIONS TO "pg_read_all_settings";

-- grant future schemas from pg_use_reserved_connections to pg_read_server_files in finance_agent (database: finance_agent)
ALTER DEFAULT PRIVILEGES FOR ROLE "pg_use_reserved_connections" GRANT ALL PRIVILEGES ON SCHEMAS TO "pg_read_server_files";

-- grant future tables from pg_use_reserved_connections to pg_read_server_files in finance_agent (database: finance_agent)
ALTER DEFAULT PRIVILEGES FOR ROLE "pg_use_reserved_connections" GRANT ALL PRIVILEGES ON TABLES TO "pg_read_server_files";

-- grant future sequences from pg_use_reserved_connections to pg_read_server_files in finance_agent (database: finance_agent)
ALTER DEFAULT PRIVILEGES FOR ROLE "pg_use_reserved_connections" GRANT ALL PRIVILEGES ON SEQUENCES TO "pg_read_server_files";

-- grant future functions from pg_use_reserved_connections to pg_read_server_files in finance_agent (database: finance_agent)
ALTER DEFAULT PRIVILEGES FOR ROLE "pg_use_reserved_connections" GRANT ALL PRIVILEGES ON FUNCTIONS TO "pg_read_server_files";

-- grant future schemas from pg_use_reserved_connections to pg_signal_backend in finance_agent (database: finance_agent)
ALTER DEFAULT PRIVILEGES FOR ROLE "pg_use_reserved_connections" GRANT ALL PRIVILEGES ON SCHEMAS TO "pg_signal_backend";

-- grant future tables from pg_use_reserved_connections to pg_signal_backend in finance_agent (database: finance_agent)
ALTER DEFAULT PRIVILEGES FOR ROLE "pg_use_reserved_connections" GRANT ALL PRIVILEGES ON TABLES TO "pg_signal_backend";

-- grant future sequences from pg_use_reserved_connections to pg_signal_backend in finance_agent (database: finance_agent)
ALTER DEFAULT PRIVILEGES FOR ROLE "pg_use_reserved_connections" GRANT ALL PRIVILEGES ON SEQUENCES TO "pg_signal_backend";

-- grant future functions from pg_use_reserved_connections to pg_signal_backend in finance_agent (database: finance_agent)
ALTER DEFAULT PRIVILEGES FOR ROLE "pg_use_reserved_connections" GRANT ALL PRIVILEGES ON FUNCTIONS TO "pg_signal_backend";

-- grant future schemas from pg_use_reserved_connections to pg_stat_scan_tables in finance_agent (database: finance_agent)
ALTER DEFAULT PRIVILEGES FOR ROLE "pg_use_reserved_connections" GRANT ALL PRIVILEGES ON SCHEMAS TO "pg_stat_scan_tables";

-- grant future tables from pg_use_reserved_connections to pg_stat_scan_tables in finance_agent (database: finance_agent)
ALTER DEFAULT PRIVILEGES FOR ROLE "pg_use_reserved_connections" GRANT ALL PRIVILEGES ON TABLES TO "pg_stat_scan_tables";

-- grant future sequences from pg_use_reserved_connections to pg_stat_scan_tables in finance_agent (database: finance_agent)
ALTER DEFAULT PRIVILEGES FOR ROLE "pg_use_reserved_connections" GRANT ALL PRIVILEGES ON SEQUENCES TO "pg_stat_scan_tables";

-- grant future functions from pg_use_reserved_connections to pg_stat_scan_tables in finance_agent (database: finance_agent)
ALTER DEFAULT PRIVILEGES FOR ROLE "pg_use_reserved_connections" GRANT ALL PRIVILEGES ON FUNCTIONS TO "pg_stat_scan_tables";

-- grant future schemas from pg_use_reserved_connections to pg_write_all_data in finance_agent (database: finance_agent)
ALTER DEFAULT PRIVILEGES FOR ROLE "pg_use_reserved_connections" GRANT ALL PRIVILEGES ON SCHEMAS TO "pg_write_all_data";

-- grant future tables from pg_use_reserved_connections to pg_write_all_data in finance_agent (database: finance_agent)
ALTER DEFAULT PRIVILEGES FOR ROLE "pg_use_reserved_connections" GRANT ALL PRIVILEGES ON TABLES TO "pg_write_all_data";

-- grant future sequences from pg_use_reserved_connections to pg_write_all_data in finance_agent (database: finance_agent)
ALTER DEFAULT PRIVILEGES FOR ROLE "pg_use_reserved_connections" GRANT ALL PRIVILEGES ON SEQUENCES TO "pg_write_all_data";

-- grant future functions from pg_use_reserved_connections to pg_write_all_data in finance_agent (database: finance_agent)
ALTER DEFAULT PRIVILEGES FOR ROLE "pg_use_reserved_connections" GRANT ALL PRIVILEGES ON FUNCTIONS TO "pg_write_all_data";

-- grant future schemas from pg_use_reserved_connections to pg_write_server_files in finance_agent (database: finance_agent)
ALTER DEFAULT PRIVILEGES FOR ROLE "pg_use_reserved_connections" GRANT ALL PRIVILEGES ON SCHEMAS TO "pg_write_server_files";

-- grant future tables from pg_use_reserved_connections to pg_write_server_files in finance_agent (database: finance_agent)
ALTER DEFAULT PRIVILEGES FOR ROLE "pg_use_reserved_connections" GRANT ALL PRIVILEGES ON TABLES TO "pg_write_server_files";

-- grant future sequences from pg_use_reserved_connections to pg_write_server_files in finance_agent (database: finance_agent)
ALTER DEFAULT PRIVILEGES FOR ROLE "pg_use_reserved_connections" GRANT ALL PRIVILEGES ON SEQUENCES TO "pg_write_server_files";

-- grant future functions from pg_use_reserved_connections to pg_write_server_files in finance_agent (database: finance_agent)
ALTER DEFAULT PRIVILEGES FOR ROLE "pg_use_reserved_connections" GRANT ALL PRIVILEGES ON FUNCTIONS TO "pg_write_server_files";

-- grant future schemas from pg_use_reserved_connections to postgres in finance_agent (database: finance_agent)
ALTER DEFAULT PRIVILEGES FOR ROLE "pg_use_reserved_connections" GRANT ALL PRIVILEGES ON SCHEMAS TO "postgres";

-- grant future tables from pg_use_reserved_connections to postgres in finance_agent (database: finance_agent)
ALTER DEFAULT PRIVILEGES FOR ROLE "pg_use_reserved_connections" GRANT ALL PRIVILEGES ON TABLES TO "postgres";

-- grant future sequences from pg_use_reserved_connections to postgres in finance_agent (database: finance_agent)
ALTER DEFAULT PRIVILEGES FOR ROLE "pg_use_reserved_connections" GRANT ALL PRIVILEGES ON SEQUENCES TO "postgres";

-- grant future functions from pg_use_reserved_connections to postgres in finance_agent (database: finance_agent)
ALTER DEFAULT PRIVILEGES FOR ROLE "pg_use_reserved_connections" GRANT ALL PRIVILEGES ON FUNCTIONS TO "postgres";

-- grant future schemas from pg_write_all_data to pg_checkpoint in finance_agent (database: finance_agent)
ALTER DEFAULT PRIVILEGES FOR ROLE "pg_write_all_data" GRANT ALL PRIVILEGES ON SCHEMAS TO "pg_checkpoint";

-- grant future tables from pg_write_all_data to pg_checkpoint in finance_agent (database: finance_agent)
ALTER DEFAULT PRIVILEGES FOR ROLE "pg_write_all_data" GRANT ALL PRIVILEGES ON TABLES TO "pg_checkpoint";

-- grant future sequences from pg_write_all_data to pg_checkpoint in finance_agent (database: finance_agent)
ALTER DEFAULT PRIVILEGES FOR ROLE "pg_write_all_data" GRANT ALL PRIVILEGES ON SEQUENCES TO "pg_checkpoint";

-- grant future functions from pg_write_all_data to pg_checkpoint in finance_agent (database: finance_agent)
ALTER DEFAULT PRIVILEGES FOR ROLE "pg_write_all_data" GRANT ALL PRIVILEGES ON FUNCTIONS TO "pg_checkpoint";

-- grant future schemas from pg_write_all_data to pg_create_subscription in finance_agent (database: finance_agent)
ALTER DEFAULT PRIVILEGES FOR ROLE "pg_write_all_data" GRANT ALL PRIVILEGES ON SCHEMAS TO "pg_create_subscription";

-- grant future tables from pg_write_all_data to pg_create_subscription in finance_agent (database: finance_agent)
ALTER DEFAULT PRIVILEGES FOR ROLE "pg_write_all_data" GRANT ALL PRIVILEGES ON TABLES TO "pg_create_subscription";

-- grant future sequences from pg_write_all_data to pg_create_subscription in finance_agent (database: finance_agent)
ALTER DEFAULT PRIVILEGES FOR ROLE "pg_write_all_data" GRANT ALL PRIVILEGES ON SEQUENCES TO "pg_create_subscription";

-- grant future functions from pg_write_all_data to pg_create_subscription in finance_agent (database: finance_agent)
ALTER DEFAULT PRIVILEGES FOR ROLE "pg_write_all_data" GRANT ALL PRIVILEGES ON FUNCTIONS TO "pg_create_subscription";

-- grant future schemas from pg_write_all_data to pg_database_owner in finance_agent (database: finance_agent)
ALTER DEFAULT PRIVILEGES FOR ROLE "pg_write_all_data" GRANT ALL PRIVILEGES ON SCHEMAS TO "pg_database_owner";

-- grant future tables from pg_write_all_data to pg_database_owner in finance_agent (database: finance_agent)
ALTER DEFAULT PRIVILEGES FOR ROLE "pg_write_all_data" GRANT ALL PRIVILEGES ON TABLES TO "pg_database_owner";

-- grant future sequences from pg_write_all_data to pg_database_owner in finance_agent (database: finance_agent)
ALTER DEFAULT PRIVILEGES FOR ROLE "pg_write_all_data" GRANT ALL PRIVILEGES ON SEQUENCES TO "pg_database_owner";

-- grant future functions from pg_write_all_data to pg_database_owner in finance_agent (database: finance_agent)
ALTER DEFAULT PRIVILEGES FOR ROLE "pg_write_all_data" GRANT ALL PRIVILEGES ON FUNCTIONS TO "pg_database_owner";

-- grant future schemas from pg_write_all_data to pg_execute_server_program in finance_agent (database: finance_agent)
ALTER DEFAULT PRIVILEGES FOR ROLE "pg_write_all_data" GRANT ALL PRIVILEGES ON SCHEMAS TO "pg_execute_server_program";

-- grant future tables from pg_write_all_data to pg_execute_server_program in finance_agent (database: finance_agent)
ALTER DEFAULT PRIVILEGES FOR ROLE "pg_write_all_data" GRANT ALL PRIVILEGES ON TABLES TO "pg_execute_server_program";

-- grant future sequences from pg_write_all_data to pg_execute_server_program in finance_agent (database: finance_agent)
ALTER DEFAULT PRIVILEGES FOR ROLE "pg_write_all_data" GRANT ALL PRIVILEGES ON SEQUENCES TO "pg_execute_server_program";

-- grant future functions from pg_write_all_data to pg_execute_server_program in finance_agent (database: finance_agent)
ALTER DEFAULT PRIVILEGES FOR ROLE "pg_write_all_data" GRANT ALL PRIVILEGES ON FUNCTIONS TO "pg_execute_server_program";

-- grant future schemas from pg_write_all_data to pg_maintain in finance_agent (database: finance_agent)
ALTER DEFAULT PRIVILEGES FOR ROLE "pg_write_all_data" GRANT ALL PRIVILEGES ON SCHEMAS TO "pg_maintain";

-- grant future tables from pg_write_all_data to pg_maintain in finance_agent (database: finance_agent)
ALTER DEFAULT PRIVILEGES FOR ROLE "pg_write_all_data" GRANT ALL PRIVILEGES ON TABLES TO "pg_maintain";

-- grant future sequences from pg_write_all_data to pg_maintain in finance_agent (database: finance_agent)
ALTER DEFAULT PRIVILEGES FOR ROLE "pg_write_all_data" GRANT ALL PRIVILEGES ON SEQUENCES TO "pg_maintain";

-- grant future functions from pg_write_all_data to pg_maintain in finance_agent (database: finance_agent)
ALTER DEFAULT PRIVILEGES FOR ROLE "pg_write_all_data" GRANT ALL PRIVILEGES ON FUNCTIONS TO "pg_maintain";

-- grant future schemas from pg_write_all_data to pg_monitor in finance_agent (database: finance_agent)
ALTER DEFAULT PRIVILEGES FOR ROLE "pg_write_all_data" GRANT ALL PRIVILEGES ON SCHEMAS TO "pg_monitor";

-- grant future tables from pg_write_all_data to pg_monitor in finance_agent (database: finance_agent)
ALTER DEFAULT PRIVILEGES FOR ROLE "pg_write_all_data" GRANT ALL PRIVILEGES ON TABLES TO "pg_monitor";

-- grant future sequences from pg_write_all_data to pg_monitor in finance_agent (database: finance_agent)
ALTER DEFAULT PRIVILEGES FOR ROLE "pg_write_all_data" GRANT ALL PRIVILEGES ON SEQUENCES TO "pg_monitor";

-- grant future functions from pg_write_all_data to pg_monitor in finance_agent (database: finance_agent)
ALTER DEFAULT PRIVILEGES FOR ROLE "pg_write_all_data" GRANT ALL PRIVILEGES ON FUNCTIONS TO "pg_monitor";

-- grant future schemas from pg_write_all_data to pg_read_all_data in finance_agent (database: finance_agent)
ALTER DEFAULT PRIVILEGES FOR ROLE "pg_write_all_data" GRANT ALL PRIVILEGES ON SCHEMAS TO "pg_read_all_data";

-- grant future tables from pg_write_all_data to pg_read_all_data in finance_agent (database: finance_agent)
ALTER DEFAULT PRIVILEGES FOR ROLE "pg_write_all_data" GRANT ALL PRIVILEGES ON TABLES TO "pg_read_all_data";

-- grant future sequences from pg_write_all_data to pg_read_all_data in finance_agent (database: finance_agent)
ALTER DEFAULT PRIVILEGES FOR ROLE "pg_write_all_data" GRANT ALL PRIVILEGES ON SEQUENCES TO "pg_read_all_data";

-- grant future functions from pg_write_all_data to pg_read_all_data in finance_agent (database: finance_agent)
ALTER DEFAULT PRIVILEGES FOR ROLE "pg_write_all_data" GRANT ALL PRIVILEGES ON FUNCTIONS TO "pg_read_all_data";

-- grant future schemas from pg_write_all_data to pg_read_all_stats in finance_agent (database: finance_agent)
ALTER DEFAULT PRIVILEGES FOR ROLE "pg_write_all_data" GRANT ALL PRIVILEGES ON SCHEMAS TO "pg_read_all_stats";

-- grant future tables from pg_write_all_data to pg_read_all_stats in finance_agent (database: finance_agent)
ALTER DEFAULT PRIVILEGES FOR ROLE "pg_write_all_data" GRANT ALL PRIVILEGES ON TABLES TO "pg_read_all_stats";

-- grant future sequences from pg_write_all_data to pg_read_all_stats in finance_agent (database: finance_agent)
ALTER DEFAULT PRIVILEGES FOR ROLE "pg_write_all_data" GRANT ALL PRIVILEGES ON SEQUENCES TO "pg_read_all_stats";

-- grant future functions from pg_write_all_data to pg_read_all_stats in finance_agent (database: finance_agent)
ALTER DEFAULT PRIVILEGES FOR ROLE "pg_write_all_data" GRANT ALL PRIVILEGES ON FUNCTIONS TO "pg_read_all_stats";

-- grant future schemas from pg_write_all_data to pg_read_all_settings in finance_agent (database: finance_agent)
ALTER DEFAULT PRIVILEGES FOR ROLE "pg_write_all_data" GRANT ALL PRIVILEGES ON SCHEMAS TO "pg_read_all_settings";

-- grant future tables from pg_write_all_data to pg_read_all_settings in finance_agent (database: finance_agent)
ALTER DEFAULT PRIVILEGES FOR ROLE "pg_write_all_data" GRANT ALL PRIVILEGES ON TABLES TO "pg_read_all_settings";

-- grant future sequences from pg_write_all_data to pg_read_all_settings in finance_agent (database: finance_agent)
ALTER DEFAULT PRIVILEGES FOR ROLE "pg_write_all_data" GRANT ALL PRIVILEGES ON SEQUENCES TO "pg_read_all_settings";

-- grant future functions from pg_write_all_data to pg_read_all_settings in finance_agent (database: finance_agent)
ALTER DEFAULT PRIVILEGES FOR ROLE "pg_write_all_data" GRANT ALL PRIVILEGES ON FUNCTIONS TO "pg_read_all_settings";

-- grant future schemas from pg_write_all_data to pg_read_server_files in finance_agent (database: finance_agent)
ALTER DEFAULT PRIVILEGES FOR ROLE "pg_write_all_data" GRANT ALL PRIVILEGES ON SCHEMAS TO "pg_read_server_files";

-- grant future tables from pg_write_all_data to pg_read_server_files in finance_agent (database: finance_agent)
ALTER DEFAULT PRIVILEGES FOR ROLE "pg_write_all_data" GRANT ALL PRIVILEGES ON TABLES TO "pg_read_server_files";

-- grant future sequences from pg_write_all_data to pg_read_server_files in finance_agent (database: finance_agent)
ALTER DEFAULT PRIVILEGES FOR ROLE "pg_write_all_data" GRANT ALL PRIVILEGES ON SEQUENCES TO "pg_read_server_files";

-- grant future functions from pg_write_all_data to pg_read_server_files in finance_agent (database: finance_agent)
ALTER DEFAULT PRIVILEGES FOR ROLE "pg_write_all_data" GRANT ALL PRIVILEGES ON FUNCTIONS TO "pg_read_server_files";

-- grant future schemas from pg_write_all_data to pg_signal_backend in finance_agent (database: finance_agent)
ALTER DEFAULT PRIVILEGES FOR ROLE "pg_write_all_data" GRANT ALL PRIVILEGES ON SCHEMAS TO "pg_signal_backend";

-- grant future tables from pg_write_all_data to pg_signal_backend in finance_agent (database: finance_agent)
ALTER DEFAULT PRIVILEGES FOR ROLE "pg_write_all_data" GRANT ALL PRIVILEGES ON TABLES TO "pg_signal_backend";

-- grant future sequences from pg_write_all_data to pg_signal_backend in finance_agent (database: finance_agent)
ALTER DEFAULT PRIVILEGES FOR ROLE "pg_write_all_data" GRANT ALL PRIVILEGES ON SEQUENCES TO "pg_signal_backend";

-- grant future functions from pg_write_all_data to pg_signal_backend in finance_agent (database: finance_agent)
ALTER DEFAULT PRIVILEGES FOR ROLE "pg_write_all_data" GRANT ALL PRIVILEGES ON FUNCTIONS TO "pg_signal_backend";

-- grant future schemas from pg_write_all_data to pg_stat_scan_tables in finance_agent (database: finance_agent)
ALTER DEFAULT PRIVILEGES FOR ROLE "pg_write_all_data" GRANT ALL PRIVILEGES ON SCHEMAS TO "pg_stat_scan_tables";

-- grant future tables from pg_write_all_data to pg_stat_scan_tables in finance_agent (database: finance_agent)
ALTER DEFAULT PRIVILEGES FOR ROLE "pg_write_all_data" GRANT ALL PRIVILEGES ON TABLES TO "pg_stat_scan_tables";

-- grant future sequences from pg_write_all_data to pg_stat_scan_tables in finance_agent (database: finance_agent)
ALTER DEFAULT PRIVILEGES FOR ROLE "pg_write_all_data" GRANT ALL PRIVILEGES ON SEQUENCES TO "pg_stat_scan_tables";

-- grant future functions from pg_write_all_data to pg_stat_scan_tables in finance_agent (database: finance_agent)
ALTER DEFAULT PRIVILEGES FOR ROLE "pg_write_all_data" GRANT ALL PRIVILEGES ON FUNCTIONS TO "pg_stat_scan_tables";

-- grant future schemas from pg_write_all_data to pg_use_reserved_connections in finance_agent (database: finance_agent)
ALTER DEFAULT PRIVILEGES FOR ROLE "pg_write_all_data" GRANT ALL PRIVILEGES ON SCHEMAS TO "pg_use_reserved_connections";

-- grant future tables from pg_write_all_data to pg_use_reserved_connections in finance_agent (database: finance_agent)
ALTER DEFAULT PRIVILEGES FOR ROLE "pg_write_all_data" GRANT ALL PRIVILEGES ON TABLES TO "pg_use_reserved_connections";

-- grant future sequences from pg_write_all_data to pg_use_reserved_connections in finance_agent (database: finance_agent)
ALTER DEFAULT PRIVILEGES FOR ROLE "pg_write_all_data" GRANT ALL PRIVILEGES ON SEQUENCES TO "pg_use_reserved_connections";

-- grant future functions from pg_write_all_data to pg_use_reserved_connections in finance_agent (database: finance_agent)
ALTER DEFAULT PRIVILEGES FOR ROLE "pg_write_all_data" GRANT ALL PRIVILEGES ON FUNCTIONS TO "pg_use_reserved_connections";

-- grant future schemas from pg_write_all_data to pg_write_server_files in finance_agent (database: finance_agent)
ALTER DEFAULT PRIVILEGES FOR ROLE "pg_write_all_data" GRANT ALL PRIVILEGES ON SCHEMAS TO "pg_write_server_files";

-- grant future tables from pg_write_all_data to pg_write_server_files in finance_agent (database: finance_agent)
ALTER DEFAULT PRIVILEGES FOR ROLE "pg_write_all_data" GRANT ALL PRIVILEGES ON TABLES TO "pg_write_server_files";

-- grant future sequences from pg_write_all_data to pg_write_server_files in finance_agent (database: finance_agent)
ALTER DEFAULT PRIVILEGES FOR ROLE "pg_write_all_data" GRANT ALL PRIVILEGES ON SEQUENCES TO "pg_write_server_files";

-- grant future functions from pg_write_all_data to pg_write_server_files in finance_agent (database: finance_agent)
ALTER DEFAULT PRIVILEGES FOR ROLE "pg_write_all_data" GRANT ALL PRIVILEGES ON FUNCTIONS TO "pg_write_server_files";

-- grant future schemas from pg_write_all_data to postgres in finance_agent (database: finance_agent)
ALTER DEFAULT PRIVILEGES FOR ROLE "pg_write_all_data" GRANT ALL PRIVILEGES ON SCHEMAS TO "postgres";

-- grant future tables from pg_write_all_data to postgres in finance_agent (database: finance_agent)
ALTER DEFAULT PRIVILEGES FOR ROLE "pg_write_all_data" GRANT ALL PRIVILEGES ON TABLES TO "postgres";

-- grant future sequences from pg_write_all_data to postgres in finance_agent (database: finance_agent)
ALTER DEFAULT PRIVILEGES FOR ROLE "pg_write_all_data" GRANT ALL PRIVILEGES ON SEQUENCES TO "postgres";

-- grant future functions from pg_write_all_data to postgres in finance_agent (database: finance_agent)
ALTER DEFAULT PRIVILEGES FOR ROLE "pg_write_all_data" GRANT ALL PRIVILEGES ON FUNCTIONS TO "postgres";

-- grant future schemas from pg_write_server_files to pg_checkpoint in finance_agent (database: finance_agent)
ALTER DEFAULT PRIVILEGES FOR ROLE "pg_write_server_files" GRANT ALL PRIVILEGES ON SCHEMAS TO "pg_checkpoint";

-- grant future tables from pg_write_server_files to pg_checkpoint in finance_agent (database: finance_agent)
ALTER DEFAULT PRIVILEGES FOR ROLE "pg_write_server_files" GRANT ALL PRIVILEGES ON TABLES TO "pg_checkpoint";

-- grant future sequences from pg_write_server_files to pg_checkpoint in finance_agent (database: finance_agent)
ALTER DEFAULT PRIVILEGES FOR ROLE "pg_write_server_files" GRANT ALL PRIVILEGES ON SEQUENCES TO "pg_checkpoint";

-- grant future functions from pg_write_server_files to pg_checkpoint in finance_agent (database: finance_agent)
ALTER DEFAULT PRIVILEGES FOR ROLE "pg_write_server_files" GRANT ALL PRIVILEGES ON FUNCTIONS TO "pg_checkpoint";

-- grant future schemas from pg_write_server_files to pg_create_subscription in finance_agent (database: finance_agent)
ALTER DEFAULT PRIVILEGES FOR ROLE "pg_write_server_files" GRANT ALL PRIVILEGES ON SCHEMAS TO "pg_create_subscription";

-- grant future tables from pg_write_server_files to pg_create_subscription in finance_agent (database: finance_agent)
ALTER DEFAULT PRIVILEGES FOR ROLE "pg_write_server_files" GRANT ALL PRIVILEGES ON TABLES TO "pg_create_subscription";

-- grant future sequences from pg_write_server_files to pg_create_subscription in finance_agent (database: finance_agent)
ALTER DEFAULT PRIVILEGES FOR ROLE "pg_write_server_files" GRANT ALL PRIVILEGES ON SEQUENCES TO "pg_create_subscription";

-- grant future functions from pg_write_server_files to pg_create_subscription in finance_agent (database: finance_agent)
ALTER DEFAULT PRIVILEGES FOR ROLE "pg_write_server_files" GRANT ALL PRIVILEGES ON FUNCTIONS TO "pg_create_subscription";

-- grant future schemas from pg_write_server_files to pg_database_owner in finance_agent (database: finance_agent)
ALTER DEFAULT PRIVILEGES FOR ROLE "pg_write_server_files" GRANT ALL PRIVILEGES ON SCHEMAS TO "pg_database_owner";

-- grant future tables from pg_write_server_files to pg_database_owner in finance_agent (database: finance_agent)
ALTER DEFAULT PRIVILEGES FOR ROLE "pg_write_server_files" GRANT ALL PRIVILEGES ON TABLES TO "pg_database_owner";

-- grant future sequences from pg_write_server_files to pg_database_owner in finance_agent (database: finance_agent)
ALTER DEFAULT PRIVILEGES FOR ROLE "pg_write_server_files" GRANT ALL PRIVILEGES ON SEQUENCES TO "pg_database_owner";

-- grant future functions from pg_write_server_files to pg_database_owner in finance_agent (database: finance_agent)
ALTER DEFAULT PRIVILEGES FOR ROLE "pg_write_server_files" GRANT ALL PRIVILEGES ON FUNCTIONS TO "pg_database_owner";

-- grant future schemas from pg_write_server_files to pg_execute_server_program in finance_agent (database: finance_agent)
ALTER DEFAULT PRIVILEGES FOR ROLE "pg_write_server_files" GRANT ALL PRIVILEGES ON SCHEMAS TO "pg_execute_server_program";

-- grant future tables from pg_write_server_files to pg_execute_server_program in finance_agent (database: finance_agent)
ALTER DEFAULT PRIVILEGES FOR ROLE "pg_write_server_files" GRANT ALL PRIVILEGES ON TABLES TO "pg_execute_server_program";

-- grant future sequences from pg_write_server_files to pg_execute_server_program in finance_agent (database: finance_agent)
ALTER DEFAULT PRIVILEGES FOR ROLE "pg_write_server_files" GRANT ALL PRIVILEGES ON SEQUENCES TO "pg_execute_server_program";

-- grant future functions from pg_write_server_files to pg_execute_server_program in finance_agent (database: finance_agent)
ALTER DEFAULT PRIVILEGES FOR ROLE "pg_write_server_files" GRANT ALL PRIVILEGES ON FUNCTIONS TO "pg_execute_server_program";

-- grant future schemas from pg_write_server_files to pg_maintain in finance_agent (database: finance_agent)
ALTER DEFAULT PRIVILEGES FOR ROLE "pg_write_server_files" GRANT ALL PRIVILEGES ON SCHEMAS TO "pg_maintain";

-- grant future tables from pg_write_server_files to pg_maintain in finance_agent (database: finance_agent)
ALTER DEFAULT PRIVILEGES FOR ROLE "pg_write_server_files" GRANT ALL PRIVILEGES ON TABLES TO "pg_maintain";

-- grant future sequences from pg_write_server_files to pg_maintain in finance_agent (database: finance_agent)
ALTER DEFAULT PRIVILEGES FOR ROLE "pg_write_server_files" GRANT ALL PRIVILEGES ON SEQUENCES TO "pg_maintain";

-- grant future functions from pg_write_server_files to pg_maintain in finance_agent (database: finance_agent)
ALTER DEFAULT PRIVILEGES FOR ROLE "pg_write_server_files" GRANT ALL PRIVILEGES ON FUNCTIONS TO "pg_maintain";

-- grant future schemas from pg_write_server_files to pg_monitor in finance_agent (database: finance_agent)
ALTER DEFAULT PRIVILEGES FOR ROLE "pg_write_server_files" GRANT ALL PRIVILEGES ON SCHEMAS TO "pg_monitor";

-- grant future tables from pg_write_server_files to pg_monitor in finance_agent (database: finance_agent)
ALTER DEFAULT PRIVILEGES FOR ROLE "pg_write_server_files" GRANT ALL PRIVILEGES ON TABLES TO "pg_monitor";

-- grant future sequences from pg_write_server_files to pg_monitor in finance_agent (database: finance_agent)
ALTER DEFAULT PRIVILEGES FOR ROLE "pg_write_server_files" GRANT ALL PRIVILEGES ON SEQUENCES TO "pg_monitor";

-- grant future functions from pg_write_server_files to pg_monitor in finance_agent (database: finance_agent)
ALTER DEFAULT PRIVILEGES FOR ROLE "pg_write_server_files" GRANT ALL PRIVILEGES ON FUNCTIONS TO "pg_monitor";

-- grant future schemas from pg_write_server_files to pg_read_all_data in finance_agent (database: finance_agent)
ALTER DEFAULT PRIVILEGES FOR ROLE "pg_write_server_files" GRANT ALL PRIVILEGES ON SCHEMAS TO "pg_read_all_data";

-- grant future tables from pg_write_server_files to pg_read_all_data in finance_agent (database: finance_agent)
ALTER DEFAULT PRIVILEGES FOR ROLE "pg_write_server_files" GRANT ALL PRIVILEGES ON TABLES TO "pg_read_all_data";

-- grant future sequences from pg_write_server_files to pg_read_all_data in finance_agent (database: finance_agent)
ALTER DEFAULT PRIVILEGES FOR ROLE "pg_write_server_files" GRANT ALL PRIVILEGES ON SEQUENCES TO "pg_read_all_data";

-- grant future functions from pg_write_server_files to pg_read_all_data in finance_agent (database: finance_agent)
ALTER DEFAULT PRIVILEGES FOR ROLE "pg_write_server_files" GRANT ALL PRIVILEGES ON FUNCTIONS TO "pg_read_all_data";

-- grant future schemas from pg_write_server_files to pg_read_all_stats in finance_agent (database: finance_agent)
ALTER DEFAULT PRIVILEGES FOR ROLE "pg_write_server_files" GRANT ALL PRIVILEGES ON SCHEMAS TO "pg_read_all_stats";

-- grant future tables from pg_write_server_files to pg_read_all_stats in finance_agent (database: finance_agent)
ALTER DEFAULT PRIVILEGES FOR ROLE "pg_write_server_files" GRANT ALL PRIVILEGES ON TABLES TO "pg_read_all_stats";

-- grant future sequences from pg_write_server_files to pg_read_all_stats in finance_agent (database: finance_agent)
ALTER DEFAULT PRIVILEGES FOR ROLE "pg_write_server_files" GRANT ALL PRIVILEGES ON SEQUENCES TO "pg_read_all_stats";

-- grant future functions from pg_write_server_files to pg_read_all_stats in finance_agent (database: finance_agent)
ALTER DEFAULT PRIVILEGES FOR ROLE "pg_write_server_files" GRANT ALL PRIVILEGES ON FUNCTIONS TO "pg_read_all_stats";

-- grant future schemas from pg_write_server_files to pg_read_all_settings in finance_agent (database: finance_agent)
ALTER DEFAULT PRIVILEGES FOR ROLE "pg_write_server_files" GRANT ALL PRIVILEGES ON SCHEMAS TO "pg_read_all_settings";

-- grant future tables from pg_write_server_files to pg_read_all_settings in finance_agent (database: finance_agent)
ALTER DEFAULT PRIVILEGES FOR ROLE "pg_write_server_files" GRANT ALL PRIVILEGES ON TABLES TO "pg_read_all_settings";

-- grant future sequences from pg_write_server_files to pg_read_all_settings in finance_agent (database: finance_agent)
ALTER DEFAULT PRIVILEGES FOR ROLE "pg_write_server_files" GRANT ALL PRIVILEGES ON SEQUENCES TO "pg_read_all_settings";

-- grant future functions from pg_write_server_files to pg_read_all_settings in finance_agent (database: finance_agent)
ALTER DEFAULT PRIVILEGES FOR ROLE "pg_write_server_files" GRANT ALL PRIVILEGES ON FUNCTIONS TO "pg_read_all_settings";

-- grant future schemas from pg_write_server_files to pg_read_server_files in finance_agent (database: finance_agent)
ALTER DEFAULT PRIVILEGES FOR ROLE "pg_write_server_files" GRANT ALL PRIVILEGES ON SCHEMAS TO "pg_read_server_files";

-- grant future tables from pg_write_server_files to pg_read_server_files in finance_agent (database: finance_agent)
ALTER DEFAULT PRIVILEGES FOR ROLE "pg_write_server_files" GRANT ALL PRIVILEGES ON TABLES TO "pg_read_server_files";

-- grant future sequences from pg_write_server_files to pg_read_server_files in finance_agent (database: finance_agent)
ALTER DEFAULT PRIVILEGES FOR ROLE "pg_write_server_files" GRANT ALL PRIVILEGES ON SEQUENCES TO "pg_read_server_files";

-- grant future functions from pg_write_server_files to pg_read_server_files in finance_agent (database: finance_agent)
ALTER DEFAULT PRIVILEGES FOR ROLE "pg_write_server_files" GRANT ALL PRIVILEGES ON FUNCTIONS TO "pg_read_server_files";

-- grant future schemas from pg_write_server_files to pg_signal_backend in finance_agent (database: finance_agent)
ALTER DEFAULT PRIVILEGES FOR ROLE "pg_write_server_files" GRANT ALL PRIVILEGES ON SCHEMAS TO "pg_signal_backend";

-- grant future tables from pg_write_server_files to pg_signal_backend in finance_agent (database: finance_agent)
ALTER DEFAULT PRIVILEGES FOR ROLE "pg_write_server_files" GRANT ALL PRIVILEGES ON TABLES TO "pg_signal_backend";

-- grant future sequences from pg_write_server_files to pg_signal_backend in finance_agent (database: finance_agent)
ALTER DEFAULT PRIVILEGES FOR ROLE "pg_write_server_files" GRANT ALL PRIVILEGES ON SEQUENCES TO "pg_signal_backend";

-- grant future functions from pg_write_server_files to pg_signal_backend in finance_agent (database: finance_agent)
ALTER DEFAULT PRIVILEGES FOR ROLE "pg_write_server_files" GRANT ALL PRIVILEGES ON FUNCTIONS TO "pg_signal_backend";

-- grant future schemas from pg_write_server_files to pg_stat_scan_tables in finance_agent (database: finance_agent)
ALTER DEFAULT PRIVILEGES FOR ROLE "pg_write_server_files" GRANT ALL PRIVILEGES ON SCHEMAS TO "pg_stat_scan_tables";

-- grant future tables from pg_write_server_files to pg_stat_scan_tables in finance_agent (database: finance_agent)
ALTER DEFAULT PRIVILEGES FOR ROLE "pg_write_server_files" GRANT ALL PRIVILEGES ON TABLES TO "pg_stat_scan_tables";

-- grant future sequences from pg_write_server_files to pg_stat_scan_tables in finance_agent (database: finance_agent)
ALTER DEFAULT PRIVILEGES FOR ROLE "pg_write_server_files" GRANT ALL PRIVILEGES ON SEQUENCES TO "pg_stat_scan_tables";

-- grant future functions from pg_write_server_files to pg_stat_scan_tables in finance_agent (database: finance_agent)
ALTER DEFAULT PRIVILEGES FOR ROLE "pg_write_server_files" GRANT ALL PRIVILEGES ON FUNCTIONS TO "pg_stat_scan_tables";

-- grant future schemas from pg_write_server_files to pg_use_reserved_connections in finance_agent (database: finance_agent)
ALTER DEFAULT PRIVILEGES FOR ROLE "pg_write_server_files" GRANT ALL PRIVILEGES ON SCHEMAS TO "pg_use_reserved_connections";

-- grant future tables from pg_write_server_files to pg_use_reserved_connections in finance_agent (database: finance_agent)
ALTER DEFAULT PRIVILEGES FOR ROLE "pg_write_server_files" GRANT ALL PRIVILEGES ON TABLES TO "pg_use_reserved_connections";

-- grant future sequences from pg_write_server_files to pg_use_reserved_connections in finance_agent (database: finance_agent)
ALTER DEFAULT PRIVILEGES FOR ROLE "pg_write_server_files" GRANT ALL PRIVILEGES ON SEQUENCES TO "pg_use_reserved_connections";

-- grant future functions from pg_write_server_files to pg_use_reserved_connections in finance_agent (database: finance_agent)
ALTER DEFAULT PRIVILEGES FOR ROLE "pg_write_server_files" GRANT ALL PRIVILEGES ON FUNCTIONS TO "pg_use_reserved_connections";

-- grant future schemas from pg_write_server_files to pg_write_all_data in finance_agent (database: finance_agent)
ALTER DEFAULT PRIVILEGES FOR ROLE "pg_write_server_files" GRANT ALL PRIVILEGES ON SCHEMAS TO "pg_write_all_data";

-- grant future tables from pg_write_server_files to pg_write_all_data in finance_agent (database: finance_agent)
ALTER DEFAULT PRIVILEGES FOR ROLE "pg_write_server_files" GRANT ALL PRIVILEGES ON TABLES TO "pg_write_all_data";

-- grant future sequences from pg_write_server_files to pg_write_all_data in finance_agent (database: finance_agent)
ALTER DEFAULT PRIVILEGES FOR ROLE "pg_write_server_files" GRANT ALL PRIVILEGES ON SEQUENCES TO "pg_write_all_data";

-- grant future functions from pg_write_server_files to pg_write_all_data in finance_agent (database: finance_agent)
ALTER DEFAULT PRIVILEGES FOR ROLE "pg_write_server_files" GRANT ALL PRIVILEGES ON FUNCTIONS TO "pg_write_all_data";

-- grant future schemas from pg_write_server_files to postgres in finance_agent (database: finance_agent)
ALTER DEFAULT PRIVILEGES FOR ROLE "pg_write_server_files" GRANT ALL PRIVILEGES ON SCHEMAS TO "postgres";

-- grant future tables from pg_write_server_files to postgres in finance_agent (database: finance_agent)
ALTER DEFAULT PRIVILEGES FOR ROLE "pg_write_server_files" GRANT ALL PRIVILEGES ON TABLES TO "postgres";

-- grant future sequences from pg_write_server_files to postgres in finance_agent (database: finance_agent)
ALTER DEFAULT PRIVILEGES FOR ROLE "pg_write_server_files" GRANT ALL PRIVILEGES ON SEQUENCES TO "postgres";

-- grant future functions from pg_write_server_files to postgres in finance_agent (database: finance_agent)
ALTER DEFAULT PRIVILEGES FOR ROLE "pg_write_server_files" GRANT ALL PRIVILEGES ON FUNCTIONS TO "postgres";

-- grant future schemas from postgres to pg_checkpoint in finance_agent (database: finance_agent)
ALTER DEFAULT PRIVILEGES FOR ROLE "postgres" GRANT ALL PRIVILEGES ON SCHEMAS TO "pg_checkpoint";

-- grant future tables from postgres to pg_checkpoint in finance_agent (database: finance_agent)
ALTER DEFAULT PRIVILEGES FOR ROLE "postgres" GRANT ALL PRIVILEGES ON TABLES TO "pg_checkpoint";

-- grant future sequences from postgres to pg_checkpoint in finance_agent (database: finance_agent)
ALTER DEFAULT PRIVILEGES FOR ROLE "postgres" GRANT ALL PRIVILEGES ON SEQUENCES TO "pg_checkpoint";

-- grant future functions from postgres to pg_checkpoint in finance_agent (database: finance_agent)
ALTER DEFAULT PRIVILEGES FOR ROLE "postgres" GRANT ALL PRIVILEGES ON FUNCTIONS TO "pg_checkpoint";

-- grant future schemas from postgres to pg_create_subscription in finance_agent (database: finance_agent)
ALTER DEFAULT PRIVILEGES FOR ROLE "postgres" GRANT ALL PRIVILEGES ON SCHEMAS TO "pg_create_subscription";

-- grant future tables from postgres to pg_create_subscription in finance_agent (database: finance_agent)
ALTER DEFAULT PRIVILEGES FOR ROLE "postgres" GRANT ALL PRIVILEGES ON TABLES TO "pg_create_subscription";

-- grant future sequences from postgres to pg_create_subscription in finance_agent (database: finance_agent)
ALTER DEFAULT PRIVILEGES FOR ROLE "postgres" GRANT ALL PRIVILEGES ON SEQUENCES TO "pg_create_subscription";

-- grant future functions from postgres to pg_create_subscription in finance_agent (database: finance_agent)
ALTER DEFAULT PRIVILEGES FOR ROLE "postgres" GRANT ALL PRIVILEGES ON FUNCTIONS TO "pg_create_subscription";

-- grant future schemas from postgres to pg_database_owner in finance_agent (database: finance_agent)
ALTER DEFAULT PRIVILEGES FOR ROLE "postgres" GRANT ALL PRIVILEGES ON SCHEMAS TO "pg_database_owner";

-- grant future tables from postgres to pg_database_owner in finance_agent (database: finance_agent)
ALTER DEFAULT PRIVILEGES FOR ROLE "postgres" GRANT ALL PRIVILEGES ON TABLES TO "pg_database_owner";

-- grant future sequences from postgres to pg_database_owner in finance_agent (database: finance_agent)
ALTER DEFAULT PRIVILEGES FOR ROLE "postgres" GRANT ALL PRIVILEGES ON SEQUENCES TO "pg_database_owner";

-- grant future functions from postgres to pg_database_owner in finance_agent (database: finance_agent)
ALTER DEFAULT PRIVILEGES FOR ROLE "postgres" GRANT ALL PRIVILEGES ON FUNCTIONS TO "pg_database_owner";

-- grant future schemas from postgres to pg_execute_server_program in finance_agent (database: finance_agent)
ALTER DEFAULT PRIVILEGES FOR ROLE "postgres" GRANT ALL PRIVILEGES ON SCHEMAS TO "pg_execute_server_program";

-- grant future tables from postgres to pg_execute_server_program in finance_agent (database: finance_agent)
ALTER DEFAULT PRIVILEGES FOR ROLE "postgres" GRANT ALL PRIVILEGES ON TABLES TO "pg_execute_server_program";

-- grant future sequences from postgres to pg_execute_server_program in finance_agent (database: finance_agent)
ALTER DEFAULT PRIVILEGES FOR ROLE "postgres" GRANT ALL PRIVILEGES ON SEQUENCES TO "pg_execute_server_program";

-- grant future functions from postgres to pg_execute_server_program in finance_agent (database: finance_agent)
ALTER DEFAULT PRIVILEGES FOR ROLE "postgres" GRANT ALL PRIVILEGES ON FUNCTIONS TO "pg_execute_server_program";

-- grant future schemas from postgres to pg_maintain in finance_agent (database: finance_agent)
ALTER DEFAULT PRIVILEGES FOR ROLE "postgres" GRANT ALL PRIVILEGES ON SCHEMAS TO "pg_maintain";

-- grant future tables from postgres to pg_maintain in finance_agent (database: finance_agent)
ALTER DEFAULT PRIVILEGES FOR ROLE "postgres" GRANT ALL PRIVILEGES ON TABLES TO "pg_maintain";

-- grant future sequences from postgres to pg_maintain in finance_agent (database: finance_agent)
ALTER DEFAULT PRIVILEGES FOR ROLE "postgres" GRANT ALL PRIVILEGES ON SEQUENCES TO "pg_maintain";

-- grant future functions from postgres to pg_maintain in finance_agent (database: finance_agent)
ALTER DEFAULT PRIVILEGES FOR ROLE "postgres" GRANT ALL PRIVILEGES ON FUNCTIONS TO "pg_maintain";

-- grant future schemas from postgres to pg_monitor in finance_agent (database: finance_agent)
ALTER DEFAULT PRIVILEGES FOR ROLE "postgres" GRANT ALL PRIVILEGES ON SCHEMAS TO "pg_monitor";

-- grant future tables from postgres to pg_monitor in finance_agent (database: finance_agent)
ALTER DEFAULT PRIVILEGES FOR ROLE "postgres" GRANT ALL PRIVILEGES ON TABLES TO "pg_monitor";

-- grant future sequences from postgres to pg_monitor in finance_agent (database: finance_agent)
ALTER DEFAULT PRIVILEGES FOR ROLE "postgres" GRANT ALL PRIVILEGES ON SEQUENCES TO "pg_monitor";

-- grant future functions from postgres to pg_monitor in finance_agent (database: finance_agent)
ALTER DEFAULT PRIVILEGES FOR ROLE "postgres" GRANT ALL PRIVILEGES ON FUNCTIONS TO "pg_monitor";

-- grant future schemas from postgres to pg_read_all_data in finance_agent (database: finance_agent)
ALTER DEFAULT PRIVILEGES FOR ROLE "postgres" GRANT ALL PRIVILEGES ON SCHEMAS TO "pg_read_all_data";

-- grant future tables from postgres to pg_read_all_data in finance_agent (database: finance_agent)
ALTER DEFAULT PRIVILEGES FOR ROLE "postgres" GRANT ALL PRIVILEGES ON TABLES TO "pg_read_all_data";

-- grant future sequences from postgres to pg_read_all_data in finance_agent (database: finance_agent)
ALTER DEFAULT PRIVILEGES FOR ROLE "postgres" GRANT ALL PRIVILEGES ON SEQUENCES TO "pg_read_all_data";

-- grant future functions from postgres to pg_read_all_data in finance_agent (database: finance_agent)
ALTER DEFAULT PRIVILEGES FOR ROLE "postgres" GRANT ALL PRIVILEGES ON FUNCTIONS TO "pg_read_all_data";

-- grant future schemas from postgres to pg_read_all_stats in finance_agent (database: finance_agent)
ALTER DEFAULT PRIVILEGES FOR ROLE "postgres" GRANT ALL PRIVILEGES ON SCHEMAS TO "pg_read_all_stats";

-- grant future tables from postgres to pg_read_all_stats in finance_agent (database: finance_agent)
ALTER DEFAULT PRIVILEGES FOR ROLE "postgres" GRANT ALL PRIVILEGES ON TABLES TO "pg_read_all_stats";

-- grant future sequences from postgres to pg_read_all_stats in finance_agent (database: finance_agent)
ALTER DEFAULT PRIVILEGES FOR ROLE "postgres" GRANT ALL PRIVILEGES ON SEQUENCES TO "pg_read_all_stats";

-- grant future functions from postgres to pg_read_all_stats in finance_agent (database: finance_agent)
ALTER DEFAULT PRIVILEGES FOR ROLE "postgres" GRANT ALL PRIVILEGES ON FUNCTIONS TO "pg_read_all_stats";

-- grant future schemas from postgres to pg_read_all_settings in finance_agent (database: finance_agent)
ALTER DEFAULT PRIVILEGES FOR ROLE "postgres" GRANT ALL PRIVILEGES ON SCHEMAS TO "pg_read_all_settings";

-- grant future tables from postgres to pg_read_all_settings in finance_agent (database: finance_agent)
ALTER DEFAULT PRIVILEGES FOR ROLE "postgres" GRANT ALL PRIVILEGES ON TABLES TO "pg_read_all_settings";

-- grant future sequences from postgres to pg_read_all_settings in finance_agent (database: finance_agent)
ALTER DEFAULT PRIVILEGES FOR ROLE "postgres" GRANT ALL PRIVILEGES ON SEQUENCES TO "pg_read_all_settings";

-- grant future functions from postgres to pg_read_all_settings in finance_agent (database: finance_agent)
ALTER DEFAULT PRIVILEGES FOR ROLE "postgres" GRANT ALL PRIVILEGES ON FUNCTIONS TO "pg_read_all_settings";

-- grant future schemas from postgres to pg_read_server_files in finance_agent (database: finance_agent)
ALTER DEFAULT PRIVILEGES FOR ROLE "postgres" GRANT ALL PRIVILEGES ON SCHEMAS TO "pg_read_server_files";

-- grant future tables from postgres to pg_read_server_files in finance_agent (database: finance_agent)
ALTER DEFAULT PRIVILEGES FOR ROLE "postgres" GRANT ALL PRIVILEGES ON TABLES TO "pg_read_server_files";

-- grant future sequences from postgres to pg_read_server_files in finance_agent (database: finance_agent)
ALTER DEFAULT PRIVILEGES FOR ROLE "postgres" GRANT ALL PRIVILEGES ON SEQUENCES TO "pg_read_server_files";

-- grant future functions from postgres to pg_read_server_files in finance_agent (database: finance_agent)
ALTER DEFAULT PRIVILEGES FOR ROLE "postgres" GRANT ALL PRIVILEGES ON FUNCTIONS TO "pg_read_server_files";

-- grant future schemas from postgres to pg_signal_backend in finance_agent (database: finance_agent)
ALTER DEFAULT PRIVILEGES FOR ROLE "postgres" GRANT ALL PRIVILEGES ON SCHEMAS TO "pg_signal_backend";

-- grant future tables from postgres to pg_signal_backend in finance_agent (database: finance_agent)
ALTER DEFAULT PRIVILEGES FOR ROLE "postgres" GRANT ALL PRIVILEGES ON TABLES TO "pg_signal_backend";

-- grant future sequences from postgres to pg_signal_backend in finance_agent (database: finance_agent)
ALTER DEFAULT PRIVILEGES FOR ROLE "postgres" GRANT ALL PRIVILEGES ON SEQUENCES TO "pg_signal_backend";

-- grant future functions from postgres to pg_signal_backend in finance_agent (database: finance_agent)
ALTER DEFAULT PRIVILEGES FOR ROLE "postgres" GRANT ALL PRIVILEGES ON FUNCTIONS TO "pg_signal_backend";

-- grant future schemas from postgres to pg_stat_scan_tables in finance_agent (database: finance_agent)
ALTER DEFAULT PRIVILEGES FOR ROLE "postgres" GRANT ALL PRIVILEGES ON SCHEMAS TO "pg_stat_scan_tables";

-- grant future tables from postgres to pg_stat_scan_tables in finance_agent (database: finance_agent)
ALTER DEFAULT PRIVILEGES FOR ROLE "postgres" GRANT ALL PRIVILEGES ON TABLES TO "pg_stat_scan_tables";

-- grant future sequences from postgres to pg_stat_scan_tables in finance_agent (database: finance_agent)
ALTER DEFAULT PRIVILEGES FOR ROLE "postgres" GRANT ALL PRIVILEGES ON SEQUENCES TO "pg_stat_scan_tables";

-- grant future functions from postgres to pg_stat_scan_tables in finance_agent (database: finance_agent)
ALTER DEFAULT PRIVILEGES FOR ROLE "postgres" GRANT ALL PRIVILEGES ON FUNCTIONS TO "pg_stat_scan_tables";

-- grant future schemas from postgres to pg_use_reserved_connections in finance_agent (database: finance_agent)
ALTER DEFAULT PRIVILEGES FOR ROLE "postgres" GRANT ALL PRIVILEGES ON SCHEMAS TO "pg_use_reserved_connections";

-- grant future tables from postgres to pg_use_reserved_connections in finance_agent (database: finance_agent)
ALTER DEFAULT PRIVILEGES FOR ROLE "postgres" GRANT ALL PRIVILEGES ON TABLES TO "pg_use_reserved_connections";

-- grant future sequences from postgres to pg_use_reserved_connections in finance_agent (database: finance_agent)
ALTER DEFAULT PRIVILEGES FOR ROLE "postgres" GRANT ALL PRIVILEGES ON SEQUENCES TO "pg_use_reserved_connections";

-- grant future functions from postgres to pg_use_reserved_connections in finance_agent (database: finance_agent)
ALTER DEFAULT PRIVILEGES FOR ROLE "postgres" GRANT ALL PRIVILEGES ON FUNCTIONS TO "pg_use_reserved_connections";

-- grant future schemas from postgres to pg_write_all_data in finance_agent (database: finance_agent)
ALTER DEFAULT PRIVILEGES FOR ROLE "postgres" GRANT ALL PRIVILEGES ON SCHEMAS TO "pg_write_all_data";

-- grant future tables from postgres to pg_write_all_data in finance_agent (database: finance_agent)
ALTER DEFAULT PRIVILEGES FOR ROLE "postgres" GRANT ALL PRIVILEGES ON TABLES TO "pg_write_all_data";

-- grant future sequences from postgres to pg_write_all_data in finance_agent (database: finance_agent)
ALTER DEFAULT PRIVILEGES FOR ROLE "postgres" GRANT ALL PRIVILEGES ON SEQUENCES TO "pg_write_all_data";

-- grant future functions from postgres to pg_write_all_data in finance_agent (database: finance_agent)
ALTER DEFAULT PRIVILEGES FOR ROLE "postgres" GRANT ALL PRIVILEGES ON FUNCTIONS TO "pg_write_all_data";

-- grant future schemas from postgres to pg_write_server_files in finance_agent (database: finance_agent)
ALTER DEFAULT PRIVILEGES FOR ROLE "postgres" GRANT ALL PRIVILEGES ON SCHEMAS TO "pg_write_server_files";

-- grant future tables from postgres to pg_write_server_files in finance_agent (database: finance_agent)
ALTER DEFAULT PRIVILEGES FOR ROLE "postgres" GRANT ALL PRIVILEGES ON TABLES TO "pg_write_server_files";

-- grant future sequences from postgres to pg_write_server_files in finance_agent (database: finance_agent)
ALTER DEFAULT PRIVILEGES FOR ROLE "postgres" GRANT ALL PRIVILEGES ON SEQUENCES TO "pg_write_server_files";

-- grant future functions from postgres to pg_write_server_files in finance_agent (database: finance_agent)
ALTER DEFAULT PRIVILEGES FOR ROLE "postgres" GRANT ALL PRIVILEGES ON FUNCTIONS TO "pg_write_server_files";
