alter table tl_lafrum11_attachment drop foreign key FK389AD9A2FE939F2A;
alter table tl_lafrum11_attachment drop foreign key FK389AD9A2131CE31E;
alter table tl_lafrum11_message drop foreign key FK4A6067E8E42F4351;
alter table tl_lafrum11_message drop foreign key FK4A6067E8131CE31E;
alter table tl_lafrum11_message drop foreign key FK4A6067E824089E4D;
alter table tl_lafrum11_message drop foreign key FK4A6067E89357B45B;
alter table tl_lafrum11_message drop foreign key FK4A6067E8647A7264;
alter table tl_lafrum11_message_seq drop foreign key FKD2C71F88FE939F2A;
alter table tl_lafrum11_message_seq drop foreign key FKD2C71F8845213B4D;
alter table tl_lafrum11_tool_session drop foreign key FK5A04D7AE131CE31E;
drop table if exists tl_lafrum11_attachment;
drop table if exists tl_lafrum11_forum;
drop table if exists tl_lafrum11_forum_user;
drop table if exists tl_lafrum11_message;
drop table if exists tl_lafrum11_message_seq;
drop table if exists tl_lafrum11_tool_session;
create table tl_lafrum11_attachment (
   uid bigint not null auto_increment,
   file_version_id bigint,
   file_type varchar(255),
   file_name varchar(255),
   file_uuid bigint,
   create_date datetime,
   forum_uid bigint,
   message_uid bigint,
   primary key (uid)
);
create table tl_lafrum11_forum (
   uid bigint not null auto_increment,
   create_date datetime,
   update_date datetime,
   create_by bigint,
   title varchar(255),
   allow_anonym bit,
   run_offline bit,
   lock_on_finished bit,
   instructions varchar(255),
   online_instructions varchar(255),
   offline_instructions varchar(255),
   content_in_use bit,
   define_later bit,
   content_id bigint unique,
   allow_edit bit,
   allow_rich_editor bit,
   primary key (uid)
);
create table tl_lafrum11_forum_user (
   uid bigint not null auto_increment,
   user_id bigint,
   last_name varchar(255),
   first_name varchar(255),
   primary key (uid)
);
create table tl_lafrum11_message (
   uid bigint not null auto_increment,
   create_date datetime,
   last_reply_date datetime,
   update_date datetime,
   create_by bigint,
   modified_by bigint,
   subject varchar(255),
   body text,
   is_authored bit,
   is_anonymous bit,
   forum_session_uid bigint,
   parent_uid bigint,
   forum_uid bigint,
   reply_number integer,
   hide_flag bit,
   primary key (uid)
);
create table tl_lafrum11_message_seq (
   uid bigint not null auto_increment,
   root_message_uid bigint,
   message_uid bigint,
   message_level smallint,
   primary key (uid)
);
create table tl_lafrum11_tool_session (
   uid bigint not null auto_increment,
   session_end_date datetime,
   session_start_date datetime,
   status integer,
   forum_uid bigint,
   session_id bigint,
   primary key (uid)
);
alter table tl_lafrum11_attachment add index FK389AD9A2FE939F2A (message_uid), add constraint FK389AD9A2FE939F2A foreign key (message_uid) references tl_lafrum11_message (uid);
alter table tl_lafrum11_attachment add index FK389AD9A2131CE31E (forum_uid), add constraint FK389AD9A2131CE31E foreign key (forum_uid) references tl_lafrum11_forum (uid);
alter table tl_lafrum11_message add index FK4A6067E8E42F4351 (create_by), add constraint FK4A6067E8E42F4351 foreign key (create_by) references tl_lafrum11_forum_user (uid);
alter table tl_lafrum11_message add index FK4A6067E8131CE31E (forum_uid), add constraint FK4A6067E8131CE31E foreign key (forum_uid) references tl_lafrum11_forum (uid);
alter table tl_lafrum11_message add index FK4A6067E824089E4D (parent_uid), add constraint FK4A6067E824089E4D foreign key (parent_uid) references tl_lafrum11_message (uid);
alter table tl_lafrum11_message add index FK4A6067E89357B45B (forum_session_uid), add constraint FK4A6067E89357B45B foreign key (forum_session_uid) references tl_lafrum11_tool_session (uid);
alter table tl_lafrum11_message add index FK4A6067E8647A7264 (modified_by), add constraint FK4A6067E8647A7264 foreign key (modified_by) references tl_lafrum11_forum_user (uid);
alter table tl_lafrum11_message_seq add index FKD2C71F88FE939F2A (message_uid), add constraint FKD2C71F88FE939F2A foreign key (message_uid) references tl_lafrum11_message (uid);
alter table tl_lafrum11_message_seq add index FKD2C71F8845213B4D (root_message_uid), add constraint FKD2C71F8845213B4D foreign key (root_message_uid) references tl_lafrum11_message (uid);
alter table tl_lafrum11_tool_session add index FK5A04D7AE131CE31E (forum_uid), add constraint FK5A04D7AE131CE31E foreign key (forum_uid) references tl_lafrum11_forum (uid);
