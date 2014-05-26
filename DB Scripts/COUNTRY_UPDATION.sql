--------------------------------------------------------
--  DDL for Procedure COUNTRY_UPDATION
--------------------------------------------------------
set define off;

  CREATE OR REPLACE PROCEDURE "COUNTRY_UPDATION" is
 orig_country     varchar2(5);
 dest_country varchar2(5);

 length_loc number(5);
begin
     FOR i in(select DISTINCT rsda.origin from qms_rec_con_sellratesdtl_acc rsda  where rsda.origin_country is null )
     loop
        length_loc :=length(i.origin);
           if(length_loc>3)
         then

               SELECT COUNTRYID INTO orig_country FROM FS_FRS_PORTMASTER P WHERE P.PORTID=i.origin;

            else
                 select countryid into orig_country from fs_fr_locationmaster lm where lm.locationid=i.origin;
          end if;
          update qms_rec_con_sellratesdtl_acc rsda set rsda.origin_country=orig_country where  rsda.origin=i.origin AND RSDA.ORIGIN_COUNTRY IS NULL;
        end loop;
         FOR i in(select DISTINCT rsda.DESTINATION from qms_rec_con_sellratesdtl_acc rsda  where rsda.DESTI_country is null )
     loop
        length_loc :=length(i.DESTINATION);
           if(length_loc>3)
         then

               SELECT COUNTRYID INTO dest_country FROM FS_FRS_PORTMASTER P WHERE P.PORTID=i.destination;

            else
                 select countryid into dest_country from fs_fr_locationmaster lm where lm.locationid=i.destination;
          end if;
          update qms_rec_con_sellratesdtl_acc rsda set rsda.DESTI_country=DEST_country where  rsda.destination=i.destination AND RSDA.DESTI_COUNTRY IS NULL;
        end loop;
end COUNTRY_UPDATION;

/

/
