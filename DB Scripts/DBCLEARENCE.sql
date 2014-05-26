--------------------------------------------------------
--  DDL for Procedure DBCLEARENCE
--------------------------------------------------------
set define off;

  CREATE OR REPLACE PROCEDURE "DBCLEARENCE" is
begin
  -- Created on 8/6/2010 by SUBRAHMANYAMK
declare
  -- Local variables here
  n integer;
begin
  -- Test statements here
   n :=0;
  FOR I IN( SELECT QUOTE_ID FROM QMS_QUOTE_MASTER QM WHERE QM.QUOTE_STATUS='APP' AND QM.ACTIVE_FLAG='I' AND
            QM.TERMINAL_ID IN ('DHLAKL','DHLCHC','DHLNPE','DHLNZAD','DHLPMR','DHLWLG'))
            LOOP
                       declare
                       begin
                       Delete from QMS_QUOTE_SPOTRATES where quote_id in (select id from qms_quote_master where quote_id=I.QUOTE_ID);
                       Delete from QMS_QUOTE_ATTACHMENTDTL where quote_id in (select id from qms_quote_master where quote_id=I.QUOTE_ID);
                       Delete from QMS_QUOTE_HF_DTL where quote_id in (select id from qms_quote_master where quote_id=I.QUOTE_ID);
                       Delete from QMS_QUOTE_CONTACTDTL where quote_id in (select id from qms_quote_master where quote_id=I.QUOTE_ID);
                       Delete from QMS_QUOTE_NOTES where quote_id in (select id from qms_quote_master where quote_id=I.QUOTE_ID);
                       Delete from QMS_QUOTE_CHARGEGROUPDTL where quote_id in (select id from qms_quote_master where quote_id=I.QUOTE_ID);
                       Delete from QMS_QUOTE_RATES where quote_id in (select id from qms_quote_master where quote_id =I.QUOTE_ID);
                       Delete from QMS_QUOTES_UPDATED where quote_id in (select id from qms_quote_master where quote_id =I.QUOTE_ID);
                       Delete from QMS_QUOTE_MASTER qm where qm.quote_id=I.QUOTE_ID;
                       Delete from fs_rt_leg where rt_plan_id in (select rt_plan_id from fs_rt_plan where quote_id=I.QUOTE_ID);
                       Delete from fs_rt_plan where quote_id=I.QUOTE_ID;
                       commit;
                      exception when others then
                      dbms_output.put_line('problem with quote_id is..'||i.quote_id);
                      rollback;
                       end;
                       n :=n+1;
           END LOOP;
           dbms_output.put_line('count..'||n);
end;
end dbclearence;

/

/
