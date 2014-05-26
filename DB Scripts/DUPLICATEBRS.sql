--------------------------------------------------------
--  DDL for Procedure DUPLICATEBRS
--------------------------------------------------------
set define off;

  CREATE OR REPLACE PROCEDURE "DUPLICATEBRS" is
brCount number;
begin
/*  for i in(select distinct BD.origin,BD.destination,BD.carrier_id,BD.service_level,BM.TERMINALID from qms_buyrates_dtl bd,QMS_BUYRATES_MASTER BM where BD.BUYRATEID=BM.BUYRATEID AND (BD.LANE_NO=BM.LANE_NO OR BM.LANE_NO IS NULL) AND BD.VERSION_NO=BM.VERSION_NO AND  bd.activeinactive is null and bd.invalidate is null)
  loop
   brCount:=0 ;
      select count(*) into brCount from qms_buyrates_dtl b1,QMS_BUYRATES_MASTER B2 where B1.BUYRATEID=B2.BUYRATEID AND (B1.LANE_NO=B2.LANE_NO OR B2.LANE_NO IS NULL) AND B1.VERSION_NO=B2.VERSION_NO AND B2.TERMINALID=i.terminalid and  b1.origin=i.origin and b1.destination=i.destination and b1.carrier_id=i.carrier_id and b1.service_level=i.service_level and b1.line_no='0' and b1.activeinactive is null and b1.invalidate is null;
      if brCount>1 then
      dbms_output.put_line('origin,destination,carrier,serviceLevel..'||i.origin||', '|| i.destination||', '||i.carrier_id||','||i.service_level);
      end if;
  end loop;*/
    for i in(select distinct BD.origin,BD.destination,BD.carrier_id,BD.service_level from qms_buyrates_dtl bd WHERE  bd.activeinactive is null)
  loop
   brCount:=0 ;
     FOR J IN ( select TERMINALID,count(B2.BUYRATEID)  from qms_buyrates_dtl b1,QMS_BUYRATES_MASTER B2 where B1.BUYRATEID=B2.BUYRATEID AND (B1.LANE_NO=B2.LANE_NO OR B2.LANE_NO IS NULL) AND B1.VERSION_NO=B2.VERSION_NO  and  b1.origin=i.origin and b1.destination=i.destination and b1.carrier_id=i.carrier_id and b1.service_level=i.service_level and b1.line_no='0' and b1.activeinactive is null GROUP BY TERMINALID HAVING COUNT(B2.BUYRATEID)>1)
     Loop
    for k in (select b1.buyrateid,b1.lane_no,b1.version_no,b1.effective_from from qms_buyrates_dtl b1,QMS_BUYRATES_MASTER B2 where B1.BUYRATEID=B2.BUYRATEID AND (B1.LANE_NO=B2.LANE_NO OR B2.LANE_NO IS NULL) AND B1.VERSION_NO=B2.VERSION_NO  and  b1.origin=i.origin and b1.destination=i.destination and b1.carrier_id=i.carrier_id and b1.service_level=i.service_level and b1.line_no='0' and b1.activeinactive is null and b2.terminalid=j.terminalid order by  b1.buyrateid,b1.lane_no,b1.version_no)
    Loop
       insert into temp_table_fre_new values(k.buyrateid,k.lane_no,k.version_no,i.origin,i.destination,i.carrier_id,i.service_level,j.terminalid,k.effective_from);
      commit;
    End loop;
     End Loop;

  end loop;
end DuplicateBRs;

/

/
