--------------------------------------------------------
--  DDL for Procedure CHANGEQUOTEID3
--------------------------------------------------------
set define off;

  CREATE OR REPLACE PROCEDURE "CHANGEQUOTEID3" is
v_terminalid varchar2(10);
v_quoteid varchar2(100);
v_prequoteid varchar2(100);
begin


  for i in (select qm.quote_id,qm.prequote_id,terminal_id,version_no  from qms_Quote_master qm where  qm.id between 30001 and 60000 order by quote_id,version_no)
  Loop
   v_terminalid :=ltrim(i.terminal_id, 'DHL');
   v_quoteid    :=v_terminalid||'_'||i.quote_id;
    v_prequoteid    :=v_terminalid||'_'||i.prequote_id;
  update qms_costing_master set quote_id=v_quoteid where quote_id=i.quote_id ;
   update fs_rt_plan set quote_id=v_quoteid where quote_id=i.quote_id ;
   update qms_Quote_master set prequote_id=v_prequoteid where quote_id=i.quote_id and terminal_id=i.terminal_id and version_no=i.version_no and prequote_id not in ('0');
    update qms_Quote_master set quote_id=v_quoteid where quote_id=i.quote_id and terminal_id=i.terminal_id and version_no=i.version_no;
  End Loop;
end changequoteid3;

/

/
