-- Script to be run for LAMS 2.1 release, on LAMS 2.0.3/2.0.4 tables.
-- Adds all the data needed for branching, plus a small change to the tool table needed for tool admin screen.

ALTER TABLE lams_group
ADD COLUMN group_ui_id INT(11);

CREATE TABLE lams_input_activity (
       activity_id BIGINT(20) NOT NULL
     , input_activity_id BIGINT(20) NOT NULL
     , UNIQUE UQ_lams_input_activity_1 (activity_id, input_activity_id)
     , INDEX (activity_id)
     , CONSTRAINT FK_lams_input_activity_1 FOREIGN KEY (activity_id)
                  REFERENCES lams_learning_activity (activity_id) ON DELETE NO ACTION ON UPDATE NO ACTION
     , INDEX (activity_id)
     , CONSTRAINT FK_lams_input_activity_2 FOREIGN KEY (activity_id)
                  REFERENCES lams_learning_activity (activity_id)
)TYPE=InnoDB;


CREATE TABLE lams_branch_condition (
       condition_id BIGINT(20) NOT NULL   AUTO_INCREMENT
     , condition_ui_id INT(11)
     , order_id INT(11)
     , name VARCHAR(255) NOT NULL
     , display_name VARCHAR(255)
     , type VARCHAR(255) NOT NULL
     , start_value VARCHAR(255)
     , end_value VARCHAR(255)
     , exact_match_value VARCHAR(255)
     , PRIMARY KEY (condition_id)
)TYPE=InnoDB;

CREATE TABLE lams_branch_activity_entry (
       entry_id BIGINT(20) NOT NULL AUTO_INCREMENT
     , entry_ui_id INT(11)
     , group_id BIGINT(20)
     , sequence_activity_id BIGINT(20) NOT NULL
     , branch_activity_id BIGINT(20) NOT NULL
     , condition_id BIGINT(20)
     , PRIMARY KEY (entry_id)
     , INDEX (group_id)
     , CONSTRAINT FK_lams_group_activity_1 FOREIGN KEY (group_id)
                  REFERENCES lams_group (group_id)
     , INDEX (sequence_activity_id)
     , CONSTRAINT FK_lams_branch_map_sequence FOREIGN KEY (sequence_activity_id)
                  REFERENCES lams_learning_activity (activity_id)
     , INDEX (branch_activity_id)
     , CONSTRAINT FK_lams_branch_map_branch FOREIGN KEY (branch_activity_id)
                  REFERENCES lams_learning_activity (activity_id)
     , INDEX (condition_id)
     , CONSTRAINT FK_lams_branch_activity_entry_4 FOREIGN KEY (condition_id)
                  REFERENCES lams_branch_condition (condition_id)
)TYPE=InnoDB;

ALTER TABLE lams_learning_activity 
ADD COLUMN default_activity_id BIGINT(20)
,ADD COLUMN start_xcoord INT(11)
,ADD COLUMN start_ycoord INT(11)
,ADD COLUMN end_xcoord INT(11)
,ADD COLUMN end_ycoord INT(11)
,ADD COLUMN stop_after_activity TINYINT NOT NULL DEFAULT 0
;
 

INSERT INTO lams_learning_activity_type VALUES (10, 'BRANCHING_CHOSEN');
INSERT INTO lams_learning_activity_type VALUES (11, 'BRANCHING_GROUP');
INSERT INTO lams_learning_activity_type VALUES (12, 'BRANCHING_TOOL');

INSERT INTO lams_system_tool (system_tool_id, learning_activity_type_id, tool_display_name, description, 
	learner_url, learner_preview_url, learner_progress_url, export_pfolio_learner_url, 
	export_pfolio_class_url, monitor_url, contribute_url, create_date_time)
VALUES (6, 10, 'Monitor Chosen Branching', 'Select between multiple sequence activities, with the branch chosen in monitoring.', 
	'learning/branching.do?method=performBranching', 'learning/branching.do?method=performBranching', 
	'learning/branching.do?method=viewBranching&mode=teacher', NULL,
	'monitoring/branchingExportPortfolio?mode=teacher', 'monitoring/branching.do?method=assignBranch', 
	'monitoring/branching.do?method=assignBranch', now());

INSERT INTO lams_system_tool (system_tool_id, learning_activity_type_id, tool_display_name, description,
        learner_url, learner_preview_url, learner_progress_url, export_pfolio_learner_url,
        export_pfolio_class_url, monitor_url, contribute_url, create_date_time)
VALUES (7, 11, 'Group Based Branching', 'Select between multiple sequence activities, with the branch chosen by an existing group.',
        'learning/branching.do?method=performBranching', 'learning/branching.do?method=performBranching',
        'learning/branching.do?method=viewBranching&mode=teacher', NULL,
        'monitoring/branchingExportPortfolio?mode=teacher', 'monitoring/branching.do?method=assignBranch',
        'monitoring/branching.do?method=assignBranch', now());

INSERT INTO lams_system_tool (system_tool_id, learning_activity_type_id, tool_display_name, description,
        learner_url, learner_preview_url, learner_progress_url, export_pfolio_learner_url,
        export_pfolio_class_url, monitor_url, contribute_url, create_date_time)
VALUES (8, 12, 'Tool Output Based Branching', 'Select between multiple sequence activities, with the branch chosen on results of another activity.',
        'learning/branching.do?method=performBranching', 'learning/branching.do?method=performBranching',
        'learning/branching.do?method=viewBranching&mode=teacher', NULL, 
        'monitoring/branchingExportPortfolio?mode=teacher', 'monitoring/branching.do?method=assignBranch',
        'monitoring/branching.do?method=assignBranch', now());

-- support tools having an admin screen

ALTER TABLE lams_tool ADD COLUMN admin_url TEXT;
ALTER TABLE lams_system_tool ADD COLUMN admin_url TEXT;


ALTER TABLE lams_configuration 
ADD COLUMN description_key VARCHAR(255)
, ADD COLUMN header_name VARCHAR(50)
, ADD COLUMN format VARCHAR(30)
, ADD COLUMN required TINYINT NOT NULL DEFAULT 0;

update lams_configuration set description_key = 'config.server.url', header_name='config.header.system', format='STRING', required=1
where config_key = 'ServerURL';

update lams_configuration set description_key = 'config.server.url.context.path', header_name='config.header.system', format='STRING', required=1
where config_key = 'ServerURLContextPath';

update lams_configuration set description_key = 'config.version', header_name='config.header.system', format='STRING', required=1
where config_key = 'Version';

update lams_configuration set description_key = 'config.temp.dir', header_name='config.header.system', format='STRING', required=1
where config_key = 'TempDir';

update lams_configuration set description_key = 'config.dump.dir', header_name='config.header.system', format='STRING', required=1
where config_key = 'DumpDir';

update lams_configuration set description_key = 'config.ear.dir', header_name='config.header.system', format='STRING', required=1
where config_key = 'EARDir';

update lams_configuration set description_key = 'config.smtp.server', header_name='config.header.system', format='STRING', required=0
where config_key = 'SMTPServer';

update lams_configuration set description_key = 'config.lams.support.email', header_name='config.header.system', format='STRING', required=0
where config_key = 'LamsSupportEmail';

update lams_configuration set description_key = 'config.content.repository.path', header_name='config.header.system', format='STRING', required=1
where config_key = 'ContentRepositoryPath';

update lams_configuration set description_key = 'config.upload.file.max.size', header_name='config.header.system', format='LONG', required=1
where config_key = 'UploadFileMaxSize';

update lams_configuration set description_key = 'config.upload.large.file.max.size', header_name='config.header.system', format='LONG', required=1
where config_key = 'UploadLargeFileMaxSize';

update lams_configuration set description_key = 'config.upload.file.max.memory.size', header_name='config.header.system', format='LONG', required=1
where config_key = 'UploadFileMaxMemorySize';

update lams_configuration set description_key = 'config.executable.extensions', header_name='config.header.system', format='STRING', required=1
where config_key = 'ExecutableExtensions';

update lams_configuration set description_key = 'config.user.inactive.timeout', header_name='config.header.system', format='LONG', required=1
where config_key = 'UserInactiveTimeout';

update lams_configuration set description_key = 'config.use.cache.debug.listener', header_name='config.header.system', format='BOOLEAN', required=1
where config_key = 'UseCacheDebugListener';

update lams_configuration set description_key = 'config.cleanup.preview.older.than.days', header_name='config.header.system', format='LONG', required=1
where config_key = 'CleanupPreviewOlderThanDays';
