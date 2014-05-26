--------------------------------------------------------
--  DDL for Procedure INSERT_DATA
--------------------------------------------------------
set define off;

  CREATE OR REPLACE PROCEDURE "INSERT_DATA" 
AS
   v_addrid   NUMBER;
BEGIN
   SELECT MAX (addressid)
     INTO v_addrid
     FROM FS_ADDRESS;

   FOR i IN (SELECT   terminalid, address_line1, addressline2,
                      NVL (city, ' COUNTRY OFFICE') city, state, zipcode,
                      country_id, contact_no, fax_no, emailid
                 FROM TEMP_ADDRESS
             ORDER BY terminalid)
   LOOP
      v_addrid := v_addrid + 1;

      INSERT INTO FS_ADDRESS
                  (addressid, addressline1, addressline2, city,
                   state, zipcode, countryid, phoneno,
                   emailid, fax
                  )
           VALUES (v_addrid, i.address_line1, i.addressline2, i.city,
                   i.state, i.zipcode, i.country_id, i.contact_no,
                   i.emailid, i.fax_no
                  );

      INSERT INTO FS_FR_TERMINALMASTER
                  (terminalid, companyid, contactname, designation, notes,
                   contactaddressid, agentind, iatacode, accountno, taxregno,
                   shipmentmode, TIME_ZONE, oper_admin_flag,
                   childterminal_flag, server_time_diff, email_actve_flag,
                   actv_flag, INVALIDATE, usestockedinvoice,
                   last_updtd_timestmp)
         SELECT terminalid, companyid, contact_person, designation,
                company_name, v_addrid, agentind, iatacode, account_code,
                tax_reg_no, shipmentmode7, TIMEZONE, oper_admin_flagoa,
                childterminal_flag, server_offset_time,
                NVL (email_actve_flag, ' '), actv_flag, INVALIDATE,
                usestockedinvoice, SYSDATE
           FROM TEMP_TERMINALMASTER
          WHERE terminalid = i.terminalid;

      DBMS_OUTPUT.put_line (v_addrid);
   END LOOP;

   INSERT INTO FS_FR_LOCATIONMASTER
               (locationid, locationname, countryid, city, shipmentmode)
      SELECT location_id, location_name, country, city, shipment_mode
        FROM TEMP_LOCATIONMASTER;

   DBMS_OUTPUT.put_line (   ' Recods Inserted '
                         || SQL%ROWCOUNT
                         || '  in locationmaster'
                        );

   /* Operterminals */
   INSERT INTO FS_FR_TERMINALLOCATION
      SELECT terminalid, locationid
        FROM TEMP_TERMINALLOCATIONS;

   DBMS_OUTPUT.put_line (   ' Recods Inserted '
                         || SQL%ROWCOUNT
                         || '  terminallocation'
                        );

-- Admin terminals
   FOR j IN (SELECT DISTINCT tm.terminalid, tm.companyid, tm.notes cmpname,
                             tm.shipmentmode, tm.TIME_ZONE tzone,
                             tm.contactname cperson,
                             tm.contactaddressid addrid, tm.cc_shipmnt_flag
                        FROM TEMP_TERMINALLOCATIONS tt,
                             FS_FR_TERMINALMASTER tm
                       WHERE tt.terminalid = tm.terminalid)
   LOOP
      INSERT INTO FS_FR_GATEWAYMASTER
                  (gatewayid, gatewaytype, gatewayname, companyname,
                   contactname, contactaddressid, notes, INDICATOR,
                   TIME_ZONE, cc_shipmnt_flag, gateway_type
                  )
           VALUES (j.terminalid, 7, 'DHL GLOBAL FORWARDING', j.cmpname,
                   j.cperson, j.addrid, j.cmpname, 'S',
                   j.tzone, j.cc_shipmnt_flag, 'S'
                  );

      INSERT INTO FS_FR_TERMINALGATEWAY
                  (terminalid, gatewayid
                  )
           VALUES (j.terminalid, j.terminalid
                  );
   END LOOP;

   DBMS_OUTPUT.put_line (' Recods Inserted in  OT');

   INSERT INTO FS_FR_TERMINAL_REGN
      SELECT parent_terminal_id, child_terminal_id, 'N'
        FROM TEMP_TERMINALREGN;

   DBMS_OUTPUT.put_line (   ' Recods Inserted in termianl Region'
                         || SQL%ROWCOUNT
                        );
EXCEPTION
   WHEN OTHERS
   THEN
      DBMS_OUTPUT.put_line (SQLERRM);
END;

/

/
