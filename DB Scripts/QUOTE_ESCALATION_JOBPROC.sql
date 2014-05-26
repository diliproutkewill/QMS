--------------------------------------------------------
--  DDL for Procedure QUOTE_ESCALATION_JOBPROC
--------------------------------------------------------
set define off;

  CREATE OR REPLACE PROCEDURE "QUOTE_ESCALATION_JOBPROC" As
Begin
  -- For Warning
  /*FOR i IN

     (SELECT qm.ID, qm.quote_id qid, qm.escalated_to,

             TO_DATE

                 (Days_Cal_Fun (um.alloted_time,

                                TO_CHAR (DECODE (qm.modified_date,

                                                 NULL, qm.created_tstmp,

                                                 qm.modified_date

                                                ),

                                         'DD-MON-YY HH24:MI:SS'

                                        )

                               ),

                  'DD-MON-YY HH24:MI:SS'

                 ) esctime,

             TO_CHAR (SYSDATE, 'DD-MON-YY HH24:MI:SS') sdate,

             qm.escalation_count, um.emailid mailid

        FROM QMS_QUOTE_MASTER qm, FS_USERMASTER um

       WHERE UPPER (escalation_flag) = 'Y'

         AND qm.escalated_to = um.empid

         AND qm.active_flag  = 'A'

         AND quote_status = 'ESC'

         AND ( TRUNC

                     (Datediff

                         ('MI',

                          TO_DATE

                             (Days_Cal_Fun

                                         (um.alloted_time,

                                          TO_CHAR (DECODE (qm.modified_date,

                                                           NULL, qm.created_tstmp,

                                                           qm.modified_date

                                                          ),

                                                   'DD-MON-YY HH24:MI:SS'

                                                  )

                                         ),

                              'DD-MON-YY HH24:MI:SS'

                             ),

                          SYSDATE

                         )

                     ) > 0

               AND TRUNC

                     (Datediff

                         ('MI',

                          TO_DATE

                             (Days_Cal_Fun

                                         (um.alloted_time,

                                          TO_CHAR (DECODE (qm.modified_date,

                                                           NULL, qm.created_tstmp,

                                                           qm.modified_date

                                                          ),

                                                   'DD-MON-YY HH24:MI:SS'

                                                  )

                                         ),

                              'DD-MON-YY HH24:MI:SS'

                             ),

                          SYSDATE

                         )

                     ) < 10

             )

         AND qm.escalation_count = 0)

  LOOP

     Send_Html_Mail_Esc (i.mailid, i.qid);



     UPDATE QMS_QUOTE_MASTER

        SET escalation_count = 1

      WHERE ID = i.ID;



      commit;

  END LOOP;*/
  -- For Reescalation
  Begin
    For i In (Select Qm.Id,
                     Qm.Quote_Id Qid,
                     Qm.Escalated_To,
                     (Select Username
                        From Fs_Usermaster
                       Where Userid = Qm.Created_By
                         And Locationid = Qm.Terminal_Id) Username,
                     'webmaster@dhl.com' From_Mailid,
                     Um.Alloted_Time,
                     (Select Alloted_Time
                        From Fs_Usermaster
                       Where Empid = Um.Rep_Officers_Id) Rep_Off_Time,
                     (Select Companyname
                        From Fs_Fr_Customermaster
                       Where Customerid = Qm.Customer_Id) Customername,
                     To_Date(Days_Cal_Fun(Um.Alloted_Time || ':00',
                                          To_Char(Decode(Qm.Modified_Date,
                                                         Null,
                                                         Qm.Created_Tstmp,
                                                         Qm.Modified_Date),
                                                  'DD-MON-YY HH24:MI:SS')),
                             'DD-MON-YY HH24:MI:SS') Esctime,
                     To_Char(Sysdate, 'DD-MON-YY HH24:MI:SS') Sdate,
                     Qm.Escalation_Count,
                     (Select Emailid
                        From Fs_Usermaster
                       Where Empid = Um.Rep_Officers_Id) To_Mailid,
                     Um.Rep_Officers_Id Repid
                From Qms_Quote_Master Qm, Fs_Usermaster Um
               Where Upper(Escalation_Flag) = 'Y'
                 And Qm.Escalated_To = Um.Empid
                 And Qm.Active_Flag = 'A'
                 And Quote_Status = 'QUE'
                 And (Trunc(Datediff('MI',
                                     To_Date(Days_Cal_Fun(Um.Alloted_Time ||
                                                          ':00',
                                                          To_Char(Decode(Qm.Modified_Date,
                                                                         Null,
                                                                         Qm.Created_Tstmp,
                                                                         Qm.Modified_Date),
                                                                  'DD-MON-YY HH24:MI:SS')),
                                             'DD-MON-YY HH24:MI:SS'),
                                     Sysdate)) > 5)) Loop
      Send_Html_Mail_Esc(i.Qid,
                         i.From_Mailid,
                         i.Username,
                         i.To_Mailid,
                         i.Customername,
                         i.Rep_Off_Time);
      Update Qms_Quote_Master
         Set Escalation_Count = 0,
             Escalated_To     = i.Repid,
             Modified_Date    = Sysdate
       Where Id = i.Id;
      Commit;
    End Loop;
  Exception
    When Others Then
      Qms_Quote_Pack.g_Err      := '<< ' || Sqlerrm || ' >>';
      Qms_Quote_Pack.g_Err_Code := '<< ' || Sqlcode || ' >>';
      Insert Into Qms_Objects_Errors
        (Ex_Date, Module_Name, Errorcode, Errormessage)
      Values
        (Sysdate,
         'jobescalation_PROC',
         Qms_Quote_Pack.g_Err_Code,
         Qms_Quote_Pack.g_Err);
      Commit;
  End;
End;

/

/
