<?xml version="1.0"?>
<!DOCTYPE queryset PUBLIC "-//OpenACS//DTD XQL 1.0//EN" "http://www.thecodemill.biz/repository/xql.dtd">
<!-- packages/forums/tcl/forums-lams-procs.xql -->
<!-- @author Ernie Ghiglione (ErnieG@melcoe.mq.edu.au) -->
<!-- @creation-date 2008-07-23 -->
<!-- @arch-tag: D5005B15-0411-44FA-B023-5588A3AE264E -->
<!-- @cvs-id $Id$ -->

<queryset>

    <fullquery name="forum::lams::is_lams.is_lams">
        <querytext>
            update forums_forums
            set is_lams= true
            where forum_id = :forum_id
        </querytext>
    </fullquery>

    <fullquery name="forum::lams::update_user_id.update_user_id">
        <querytext>
            update acs_objects
             set modifying_user = :user_id,
             creation_user = :user_id
            where object_id = :forum_id
        </querytext>
    </fullquery>

  <fullquery name="forum::lams::get_package_instance.get_package_id">
    <querytext>
      select 
       dca.package_id
      from 
       dotlrn_community_applets dca,apm_packages ap
      where 
       community_id=:community_id and 
       ap.package_id=dca.package_id and 
       ap.package_key='forums'
    </querytext>
  </fullquery>

  <fullquery name="forum::lams::output_number_of_postings.number_of_postings">
    <querytext>
      select count(user_id) from forums_messages where forum_id = :forum_id and user_id = :user_id
    </querytext>
  </fullquery>

  <fullquery name="forum::lams::output_number_of_words.number_of_words">
    <querytext>
      select format, content from forums_messages where forum_id = :forum_id and user_id = :user_id
    </querytext>
  </fullquery>

</queryset>