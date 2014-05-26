--------------------------------------------------------
--  DDL for Procedure HTML_EMAIL
--------------------------------------------------------
set define off;

  CREATE OR REPLACE PROCEDURE "HTML_EMAIL" (p_To            In Varchar2,
                                       p_From          In Varchar2,
                                       p_Subject       In Varchar2,
                                       p_Text          In Varchar2 Default Null,
                                       p_Html          In Varchar2 Default Null,
                                       p_Smtp_Hostname In Varchar2,
                                       p_Smtp_Portnum  In Varchar2) Is
  l_Boundary   Varchar2(255) Default 'a1b2c3d4e3f2g1';
  l_Connection Utl_Smtp.Connection;
  l_Body_Html  Clob := Empty_Clob;
  --This LOB will be the email message
  l_Offset   Number;
  l_Ammount  Number;
  l_Temp     Varchar2(32767) Default Null;
  l_Dest     Varchar2(32767);
  l_Dest_Tmp Varchar2(32767);
Begin
  l_Dest       := p_To;
  l_Connection := Utl_Smtp.Open_Connection(p_Smtp_Hostname, p_Smtp_Portnum);
  Utl_Smtp.Helo(l_Connection, p_Smtp_Hostname);
  Utl_Smtp.Mail(l_Connection, p_From);
  While Instr(l_Dest, ',') != 0 Loop
    l_Dest_Tmp := Substr(l_Dest, 1, Instr(l_Dest, ',') - 1);
    l_Dest     := Substr(l_Dest, Instr(l_Dest, ',') + 1);
    Utl_Smtp.Rcpt(l_Connection, l_Dest_Tmp);
  End Loop;
  Utl_Smtp.Rcpt(l_Connection, l_Dest);
  l_Temp := l_Temp || 'MIME-Version: 1.0' || Chr(13) || Chr(10);
  l_Temp := l_Temp || 'To: ' || p_To || Chr(13) || Chr(10);
  l_Temp := l_Temp || 'From: ' || p_From || Chr(13) || Chr(10);
  l_Temp := l_Temp || 'Subject: ' || p_Subject || Chr(13) || Chr(10);
  l_Temp := l_Temp || 'Reply-To: ' || p_From || Chr(13) || Chr(10);
  l_Temp := l_Temp || 'Content-Type: multipart/alternative; boundary=' ||
            Chr(34) || l_Boundary || Chr(34) || Chr(13) || Chr(10);
  ----------------------------------------------------
  -- Write the headers
  Dbms_Lob.Createtemporary(l_Body_Html, False, 10);
  Dbms_Lob.Write(l_Body_Html, Length(l_Temp), 1, l_Temp);
  ----------------------------------------------------
  -- Write the text boundary
  l_Offset := Dbms_Lob.Getlength(l_Body_Html) + 1;
  l_Temp   := '--' || l_Boundary || Chr(13) || Chr(10);
  l_Temp   := l_Temp || 'content-type: text/plain; charset=windows-1252' ||
              Chr(13) || Chr(10) || Chr(13) || Chr(10);
  Dbms_Lob.Write(l_Body_Html, Length(l_Temp), l_Offset, l_Temp);
  ----------------------------------------------------
  -- Write the plain text portion of the email
  l_Offset := Dbms_Lob.Getlength(l_Body_Html) + 1;
  Dbms_Lob.Write(l_Body_Html, Length(l_Temp), l_Offset, l_Temp);
  ----------------------------------------------------
  -- Write the HTML boundary
  l_Temp   := Chr(13) || Chr(10) || Chr(13) || Chr(10) || '--' ||
              l_Boundary || Chr(13) || Chr(10);
  l_Temp   := l_Temp || 'content-type: text/html; charset=windows-1252' ||
              Chr(13) || Chr(10) || Chr(13) || Chr(10);
  l_Offset := Dbms_Lob.Getlength(l_Body_Html) + 1;
  Dbms_Lob.Write(l_Body_Html, Length(l_Temp), l_Offset, l_Temp);
  ----------------------------------------------------
  -- Write the HTML portion of the message
  l_Offset := Dbms_Lob.Getlength(l_Body_Html) + 1;
  Dbms_Lob.Write(l_Body_Html, Length(p_Html), l_Offset, p_Html);
  ----------------------------------------------------
  -- Write the final html boundary
  l_Temp   := Chr(13) || Chr(10) || '--' || l_Boundary || '--' || Chr(13);
  l_Offset := Dbms_Lob.Getlength(l_Body_Html) + 1;
  Dbms_Lob.Write(l_Body_Html, Length(l_Temp), l_Offset, l_Temp);
  ----------------------------------------------------
  -- Send the email in 1900 byte chunks to UTL_SMTP
  l_Offset  := 1;
  l_Ammount := 1900;
  Utl_Smtp.Open_Data(l_Connection);
  While l_Offset < Dbms_Lob.Getlength(l_Body_Html) Loop
    Utl_Smtp.Write_Data(l_Connection,
                        Dbms_Lob.Substr(l_Body_Html, l_Ammount, l_Offset));
    l_Offset  := l_Offset + l_Ammount;
    l_Ammount := Least(1900, Dbms_Lob.Getlength(l_Body_Html) - l_Ammount);
  End Loop;
  Utl_Smtp.Close_Data(l_Connection);
  Utl_Smtp.Quit(l_Connection);
  Dbms_Lob.Freetemporary(l_Body_Html);
Exception
  When Others Then
    --print_out('Exception.' || SQLERRM);
    Rollback;
End;

/

/
