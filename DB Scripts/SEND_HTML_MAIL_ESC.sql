--------------------------------------------------------
--  DDL for Procedure SEND_HTML_MAIL_ESC
--------------------------------------------------------
set define off;

  CREATE OR REPLACE PROCEDURE "SEND_HTML_MAIL_ESC" (p_Quoteid Varchar2,
                                               p_Frommailid Varchar2,
                                               p_Username Varchar2,
                                               p_Tomailid Varchar2,
                                               p_Customername Varchar2,
                                               p_Approvaltime Varchar2) Is
  Content Varchar2(32767) := '';
  Mailhost Varchar2(64) := 'apollo.apis.dhl.com';
  Sender Varchar2(64) := p_Frommailid;
  Recipient Varchar2(64) := p_Tomailid;
  Mail_Conn Utl_Smtp.Connection;
  Temp_Userid Varchar2(64);
  Subject Varchar2(200) := 'Quote Pending Your Approval, ' ||
                           p_Customername || ' , ' || p_Quoteid;
  l_Boundary Varchar2(255) Default '';
  l_Body_Html Clob := Empty_Clob;
  --This LOB will be the email message
  l_Offset Number;
  l_Ammount Number;
  p_Text Varchar2(400) := '';
  l_Temp Varchar2(32767) Default Null;
  Html_Body Varchar2(32767) := '<html><body bgcolor=#FFFFFF>';
Begin
  Html_Body := '<html><body bgcolor=#FFFFFF>';
  Html_Body := Html_Body || 'Please refer to Quote Reference ' || p_Quoteid ||
               ' pending your approval.' || ' Quote was created by ' ||
               p_Username || ' for ' || p_Customername || '. ' ||
               'Quote must be reviewed within ' || p_Approvaltime ||
               ' hours.';
  /*p_text :=

    'Please refer to Quote Reference '

  || p_quoteid

  || ' pending your approval.'

  || ' Quote was created by '

  || P_username

  || ' for '

  || p_customername

  || '.'

  || 'Quote must be reviewed with in '

  || p_approvaltime

  || 'hours.';*/
  Html_Body := Html_Body || '</body></html>';
  Html_Email(Recipient, Sender, Subject, p_Text, Html_Body, Mailhost, '25');
Exception
  When Others Then
    Dbms_Output.Put_Line('Exception........................' || Sqlerrm);
    Qms_Quote_Pack.g_Err := '<< ' || Sqlerrm || ' >>';
    Qms_Quote_Pack.g_Err_Code := '<< ' || Sqlcode || ' >>';
    Insert Into Qms_Objects_Errors
      (Ex_Date, Module_Name, Errorcode, Errormessage)
    Values
      (Sysdate,
       'jobescalation_PROC',
       Qms_Quote_Pack.g_Err_Code,
       Qms_Quote_Pack.g_Err);
    Commit;
    Null;
End;

/

/
