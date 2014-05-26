--------------------------------------------------------
--  DDL for Procedure REJ_GEN_APP_AU_NZ_DEL_PRC
--------------------------------------------------------
set define off;

  CREATE OR REPLACE PROCEDURE "REJ_GEN_APP_AU_NZ_DEL_PRC" is
begin
  -- Created on 8/6/2010 by SUBRAHMANYAMK
  declare
    -- Local variables here
    n integer;
  begin
    -- Test statements here
    n := 0;
    FOR I IN (SELECT QUOTE_ID, QUOTE_STATUS, SNO
                FROM REJ_GEN_APP_AU_NZ_DELETION WHERE SNO >8000 ) LOOP
      IF I.QUOTE_STATUS = 'REJ' THEN
        declare
        begin
          --SPOT RATES
          INSERT INTO TEMP_SPORTRATES
            SELECT *
              FROM QMS_QUOTE_SPOTRATES
             where quote_id in
                   (select id
                      from qms_quote_master
                     where quote_id = I.QUOTE_ID
                       AND ACTIVE_FLAG IN ('A', 'I'));

          Delete from QMS_QUOTE_SPOTRATES
           where quote_id in (select id
                                from qms_quote_master
                               where quote_id = I.QUOTE_ID
                                 AND ACTIVE_FLAG IN ('A', 'I'));
          --ATTACHEMENTS
          INSERT INTO TEMP_ATTACHMENT
            SELECT *
              FROM QMS_QUOTE_ATTACHMENTDTL
             where quote_id in
                   (select id
                      from qms_quote_master
                     where quote_id = I.QUOTE_ID
                       AND ACTIVE_FLAG IN ('A', 'I'));
          Delete from QMS_QUOTE_ATTACHMENTDTL
           where quote_id in (select id
                                from qms_quote_master
                               where quote_id = I.QUOTE_ID
                                 AND ACTIVE_FLAG IN ('A', 'I'));
          --HEADER FOOTER
          INSERT INTO TEMP_HEADERFOOTER
            SELECT *
              FROM QMS_QUOTE_HF_DTL
             where quote_id in
                   (select id
                      from qms_quote_master
                     where quote_id = I.QUOTE_ID
                       AND ACTIVE_FLAG IN ('A', 'I'));

          Delete from QMS_QUOTE_HF_DTL
           where quote_id in (select id
                                from qms_quote_master
                               where quote_id = I.QUOTE_ID
                                 AND ACTIVE_FLAG IN ('A', 'I'));
          --CONTACTS
          INSERT INTO TEMP_CONTACTS
            SELECT *
              FROM QMS_QUOTE_CONTACTDTL
             where quote_id in
                   (select id
                      from qms_quote_master
                     where quote_id = I.QUOTE_ID
                       AND ACTIVE_FLAG IN ('A', 'I'));
          Delete from QMS_QUOTE_CONTACTDTL
           where quote_id in (select id
                                from qms_quote_master
                               where quote_id = I.QUOTE_ID
                                 AND ACTIVE_FLAG IN ('A', 'I'));
          --NOTES
          INSERT INTO TEMP_NOTES
            SELECT *
              FROM QMS_QUOTE_NOTES
             where quote_id in
                   (select id
                      from qms_quote_master
                     where quote_id = I.QUOTE_ID
                       AND ACTIVE_FLAG IN ('A', 'I'));
          Delete from QMS_QUOTE_NOTES
           where quote_id in (select id
                                from qms_quote_master
                               where quote_id = I.QUOTE_ID
                                 AND ACTIVE_FLAG IN ('A', 'I'));
          --quote charge group
          INSERT INTO TEMP_QUOTE_CHARGE
            SELECT *
              FROM QMS_QUOTE_CHARGEGROUPDTL
             where quote_id in
                   (select id
                      from qms_quote_master
                     where quote_id = I.QUOTE_ID
                       AND ACTIVE_FLAG IN ('A', 'I'));

          Delete from QMS_QUOTE_CHARGEGROUPDTL
           where quote_id in (select id
                                from qms_quote_master
                               where quote_id = I.QUOTE_ID
                                 AND ACTIVE_FLAG IN ('A', 'I'));
          --TEMP RATES
          INSERT INTO TEMP_RATES
            SELECT *
              FROM QMS_QUOTE_RATES
             where quote_id in
                   (select id
                      from qms_quote_master
                     where quote_id = I.QUOTE_ID
                       AND ACTIVE_FLAG IN ('A', 'I'));
          Delete from QMS_QUOTE_RATES
           where quote_id in (select id
                                from qms_quote_master
                               where quote_id = I.QUOTE_ID
                                 AND ACTIVE_FLAG IN ('A', 'I'));
          --UPDATED
          INSERT INTO TEMP_UPDATED
            SELECT *
              FROM QMS_QUOTES_UPDATED
             where quote_id in
                   (select id
                      from qms_quote_master
                     where quote_id = I.QUOTE_ID
                       AND ACTIVE_FLAG IN ('A', 'I'));

          Delete from QMS_QUOTES_UPDATED
           where quote_id in (select id
                                from qms_quote_master
                               where quote_id = I.QUOTE_ID
                                 AND ACTIVE_FLAG IN ('A', 'I'));
          --MASTER
          INSERT INTO TEMP_QUOTE_MASTER
            SELECT *
              FROM QMS_QUOTE_MASTER qm
             where qm.quote_id = I.QUOTE_ID
               AND ACTIVE_FLAG IN ('A', 'I');

          Delete from QMS_QUOTE_MASTER qm
           where qm.quote_id = I.QUOTE_ID
             AND ACTIVE_FLAG IN ('A', 'I');
          /*         Delete from fs_rt_leg
                     where rt_plan_id in
                           (select rt_plan_id
                              from fs_rt_plan
                             where quote_id = I.QUOTE_ID
                               AND ACTIVE_FLAG IN ('A', 'I'));
                    Delete from fs_rt_plan
                     where quote_id = I.QUOTE_ID
                       AND ACTIVE_FLAG IN ('A', 'I');
          */
        exception
          when others then
            /* dbms_output.put_line('problem with quote_id is..' ||
                                 i.quote_id);
            --  rollback;*/
            UPDATE REJ_GEN_APP_AU_NZ_DELETION
               SET REMARKS = 'HAVING SOME PROBLME WITH THIS.'
             WHERE SNO = I.SNO;
        end;

      ELSIF I.QUOTE_STATUS IN ('APP', 'GEN') THEN
        declare
        begin
          INSERT INTO TEMP_SPORTRATES
            SELECT *
              FROM QMS_QUOTE_SPOTRATES
             where quote_id in (select id
                                  from qms_quote_master
                                 where quote_id = I.QUOTE_ID
                                   AND ACTIVE_FLAG = 'I');

          Delete from QMS_QUOTE_SPOTRATES
           where quote_id in (select id
                                from qms_quote_master
                               where quote_id = I.QUOTE_ID
                                 AND ACTIVE_FLAG = 'I');
          INSERT INTO TEMP_ATTACHMENT
            SELECT *
              FROM QMS_QUOTE_ATTACHMENTDTL
             where quote_id in (select id
                                  from qms_quote_master
                                 where quote_id = I.QUOTE_ID
                                   AND ACTIVE_FLAG = 'I');

          Delete from QMS_QUOTE_ATTACHMENTDTL
           where quote_id in (select id
                                from qms_quote_master
                               where quote_id = I.QUOTE_ID
                                 AND ACTIVE_FLAG = 'I');
          INSERT INTO TEMP_HEADERFOOTER
            SELECT *
              FROM QMS_QUOTE_HF_DTL
             where quote_id in (select id
                                  from qms_quote_master
                                 where quote_id = I.QUOTE_ID
                                   AND ACTIVE_FLAG = 'I');

          Delete from QMS_QUOTE_HF_DTL
           where quote_id in (select id
                                from qms_quote_master
                               where quote_id = I.QUOTE_ID
                                 AND ACTIVE_FLAG = 'I');
          INSERT INTO TEMP_CONTACTS
            SELECT *
              FROM QMS_QUOTE_CONTACTDTL
             where quote_id in (select id
                                  from qms_quote_master
                                 where quote_id = I.QUOTE_ID
                                   AND ACTIVE_FLAG = 'I');

          Delete from QMS_QUOTE_CONTACTDTL
           where quote_id in (select id
                                from qms_quote_master
                               where quote_id = I.QUOTE_ID
                                 AND ACTIVE_FLAG = 'I');
          INSERT INTO TEMP_NOTES
            SELECT *
              FROM QMS_QUOTE_NOTES
             where quote_id in (select id
                                  from qms_quote_master
                                 where quote_id = I.QUOTE_ID
                                   AND ACTIVE_FLAG = 'I');
          Delete from QMS_QUOTE_NOTES
           where quote_id in (select id
                                from qms_quote_master
                               where quote_id = I.QUOTE_ID
                                 AND ACTIVE_FLAG = 'I');
          INSERT INTO TEMP_QUOTE_CHARGE
            SELECT *
              FROM QMS_QUOTE_CHARGEGROUPDTL
             where quote_id in (select id
                                  from qms_quote_master
                                 where quote_id = I.QUOTE_ID
                                   AND ACTIVE_FLAG = 'I');
          Delete from QMS_QUOTE_CHARGEGROUPDTL
           where quote_id in (select id
                                from qms_quote_master
                               where quote_id = I.QUOTE_ID
                                 AND ACTIVE_FLAG = 'I');
          INSERT INTO TEMP_RATES
            SELECT *
              FROM QMS_QUOTE_RATES
             where quote_id in (select id
                                  from qms_quote_master
                                 where quote_id = I.QUOTE_ID
                                   AND ACTIVE_FLAG = 'I');

          Delete from QMS_QUOTE_RATES
           where quote_id in (select id
                                from qms_quote_master
                               where quote_id = I.QUOTE_ID
                                 AND ACTIVE_FLAG = 'I');
          INSERT INTO TEMP_UPDATED SELECT * FROM QMS_QUOTES_UPDATED
           where quote_id in (select id
                                from qms_quote_master
                               where quote_id = I.QUOTE_ID
                                 AND ACTIVE_FLAG = 'I');

          Delete from QMS_QUOTES_UPDATED
           where quote_id in (select id
                                from qms_quote_master
                               where quote_id = I.QUOTE_ID
                                 AND ACTIVE_FLAG = 'I');
INSERT INTO TEMP_QUOTE_MASTER SELECT * FROM   QMS_QUOTE_MASTER qm
           where qm.quote_id = I.QUOTE_ID
             AND ACTIVE_FLAG = 'I';

          Delete from QMS_QUOTE_MASTER qm
           where qm.quote_id = I.QUOTE_ID
             AND ACTIVE_FLAG = 'I';
          /*         Delete from fs_rt_leg
                     where rt_plan_id in
                           (select rt_plan_id
                              from fs_rt_plan
                             where quote_id = I.QUOTE_ID
                               AND ACTIVE_FLAG IN ('A', 'I'));
                    Delete from fs_rt_plan
                     where quote_id = I.QUOTE_ID
                       AND ACTIVE_FLAG IN ('A', 'I');
          */
        exception
          when others then
            /* dbms_output.put_line('problem with quote_id is..' ||
                                 i.quote_id);
            --  rollback;*/
            UPDATE REJ_GEN_APP_AU_NZ_DELETION
               SET REMARKS = 'HAVING SOME PROBLME WITH THIS.'
             WHERE SNO = I.SNO;
        end;

      END IF;
      n := n + 1;
    END LOOP;
    dbms_output.put_line('count..' || n);
  end;
end REJ_GEN_APP_AU_NZ_DEL_PRC;

/

/
