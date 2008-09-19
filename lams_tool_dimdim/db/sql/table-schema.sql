alter table tl_laddim10_attachment drop foreign key fk_laddim10_dimdim_attachment_to_dimdim;
alter table tl_laddim10_session drop foreign key fk_laddim10_dimdim_session_to_dimdim;
alter table tl_laddim10_user drop foreign key fk_laddim10_dimdim_user_to_dimdim_session;
drop table if exists tl_laddim10_attachment;
drop table if exists tl_laddim10_dimdim;
drop table if exists tl_laddim10_dimdim_config;
drop table if exists tl_laddim10_session;
drop table if exists tl_laddim10_user;
create table tl_laddim10_attachment (uid bigint not null auto_increment, file_version_id bigint, file_type varchar(255), file_name varchar(255), file_uuid bigint, create_date datetime, dimdim_uid bigint, primary key (uid));
create table tl_laddim10_dimdim (uid bigint not null auto_increment, create_date datetime, update_date datetime, create_by bigint, title varchar(255), instructions text, run_offline bit, lock_on_finished bit, allow_rich_editor bit, online_instructions text, offline_instructions text, content_in_use bit, define_later bit, tool_content_id bigint, topic varchar(255), max_attendee_mikes integer, primary key (uid));
create table tl_laddim10_dimdim_config (uid bigint not null auto_increment, config_key varchar(255), config_value varchar(255), primary key (uid));
create table tl_laddim10_session (uid bigint not null auto_increment, session_end_date datetime, session_start_date datetime, status integer, session_id bigint, session_name varchar(250), dimdim_uid bigint, topic varchar(255), meeting_key varchar(255), max_attendee_mikes integer, primary key (uid));
create table tl_laddim10_user (uid bigint not null auto_increment, user_id bigint, last_name varchar(255), login_name varchar(255), first_name varchar(255), finishedActivity bit, dimdim_session_uid bigint, entry_uid bigint, primary key (uid));
alter table tl_laddim10_attachment add index fk_laddim10_dimdim_attachment_to_dimdim (dimdim_uid), add constraint fk_laddim10_dimdim_attachment_to_dimdim foreign key (dimdim_uid) references tl_laddim10_dimdim (uid);
alter table tl_laddim10_session add index fk_laddim10_dimdim_session_to_dimdim (dimdim_uid), add constraint fk_laddim10_dimdim_session_to_dimdim foreign key (dimdim_uid) references tl_laddim10_dimdim (uid);
alter table tl_laddim10_user add index fk_laddim10_dimdim_user_to_dimdim_session (dimdim_session_uid), add constraint fk_laddim10_dimdim_user_to_dimdim_session foreign key (dimdim_session_uid) references tl_laddim10_session (uid);
