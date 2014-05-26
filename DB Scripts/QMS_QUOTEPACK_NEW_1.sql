--------------------------------------------------------
--  DDL for Package Body QMS_QUOTEPACK_NEW
--------------------------------------------------------

  CREATE OR REPLACE PACKAGE BODY "QMS_QUOTEPACK_NEW" AS

  /*
  This procedure is called whenever any rate (BR/RSR/CSR) or Charges/Cartages get
  updated. This procedure scans for all the active, complete quotes which have the
  quote status in one of the following:
  GEN, PEN, NAC, ACC
  It then looks for the rate/charge/cartage that is getting updated in those quotes
  and if found, the quote is updated with an entry in the QMS_QUOTES_UPDATED table.


  The IN Parameters are:
     p_old_sellcharge_id   NUMBER,
     p_old_buycharge_id    NUMBER,
     p_old_lane_no         NUMBER,
     p_new_sellcharge_id   NUMBER,
     p_new_buycharge_id    NUMBER,
     p_new_lane_no         NUMBER,
     p_sell_buy_flag       VARCHAR2,
     p_charge_type         VARCHAR2,
     p_zone_code           VARCHAR2,
     p_changedesc          VARCHAR2
  */
  PROCEDURE Qms_Quote_Update(p_Old_Sellcharge_Id NUMBER,
                             p_Old_Buycharge_Id  NUMBER,
                             p_Old_Lane_No       NUMBER,
                             p_Old_Version_No    NUMBER,--@@Added for the WPBN issues-146448,146968 on 19/12/08
                             p_New_Version_No    NUMBER,
                             p_New_Sellcharge_Id NUMBER,
                             p_New_Buycharge_Id  NUMBER,
                             p_New_Lane_No       NUMBER,
                             p_Sell_Buy_Flag     VARCHAR2,
                             p_Charge_Type       VARCHAR2,
                             p_Zone_Code         VARCHAR2,
                             p_Changedesc        VARCHAR2) AS

    g_Err         VARCHAR2(1000) := '';
    g_Err_Code    VARCHAR2(100) := '';
    v_charge_type VARCHAR2(10) := '';
    v_ship_mode   VARCHAR2(3) := '';
    /*v_flag       VARCHAR2 (10)   := '';*/
  BEGIN

    /*    Select Substr(p_Charge_Type, Instr(p_Charge_Type, '@') + 1),
         Substr(p_Charge_Type, 1, Instr(p_Charge_Type, '@') - 1)
    Into v_Charge_Type, v_Ship_Mode
    From Dual;*/

    v_Charge_Type := SUBSTR(p_Charge_Type, INSTR(p_Charge_Type, '@') + 1);
    v_Ship_Mode   := SUBSTR(p_Charge_Type, 1, INSTR(p_Charge_Type, '@') - 1);

    IF p_Sell_Buy_Flag = 'BR' THEN
      BEGIN
        /*Delete qms_quotes_updated qr
        where qr.sell_buy_flag = p_sell_buy_flag
          and qr.new_buycharge_id = p_Old_buycharge_id
          and qr.new_lane_no = p_Old_lane_no;*/
        FOR i IN (SELECT Qr.Quote_Id,
                         Qr.Sell_Buy_Flag,
                         Qr.New_Buycharge_Id,
                         Qr.New_Lane_No,
                         Qr.New_Version_No --@@Added for the WPBN issues -146448,146968 on 19/12/08
                    FROM QMS_QUOTES_UPDATED Qr
                   WHERE Qr.Sell_Buy_Flag = p_Sell_Buy_Flag
                     AND Qr.New_Buycharge_Id = p_Old_Buycharge_Id
                     AND Qr.New_Lane_No = p_Old_Lane_No
                      AND Confirm_Flag IS NULL) LOOP
          UPDATE QMS_QUOTES_UPDATED Qu
             SET New_Buycharge_Id = p_New_Buycharge_Id,
                 New_Lane_No      = p_New_Lane_No,
                 New_Version_No   = p_New_Version_No,--@@Added for the WPBN issues -146448,146968 on 19/12/08
                 Changedesc       = p_Changedesc
           WHERE Qu.Quote_Id = i.Quote_Id
             AND Qu.Sell_Buy_Flag = p_Sell_Buy_Flag
             AND Qu.New_Buycharge_Id = p_Old_Buycharge_Id
             AND Qu.New_Lane_No = p_Old_Lane_No
             AND Confirm_Flag IS NULL;
        END LOOP;
        FOR i IN (SELECT DISTINCT (Qr.Quote_Id)
                    FROM QMS_QUOTE_RATES Qr, QMS_QUOTE_MASTER Qm
                   WHERE Qr.Sell_Buy_Flag = p_Sell_Buy_Flag
                      --@@Commented by Kameswari  for the WPBN issue-114121 Or (Qr.Sell_Buy_Flag In ('RSR', 'CSR')
                      AND   NVL(Qr.Margin_Discount_Flag, 'M') = 'M'
                     AND Qr.Buyrate_Id = p_Old_Buycharge_Id
                     AND Qr.Rate_Lane_No = p_Old_Lane_No
                     AND Qr.Quote_Id = Qm.Id
                     /* And Qr.Line_No='0'*/
                     AND Qm.Id NOT IN (SELECT  quote_id FROM QMS_QUOTES_UPDATED WHERE sell_buy_flag=p_Sell_Buy_Flag AND confirm_flag IS NULL)
                    AND Qm.Active_Flag = 'A')
                    /* And Qm.Id = (Select Id
                                    From Qms_Quote_Master
                                   Where Quote_Id = Qm.Quote_Id
                                     And Version_No In
                                         (Select Max(Version_No)
                                            From Qms_Quote_Master
                                           Where Id = Qr.Quote_Id))
                     And Qm.Complete_Flag = 'C'
                        And Qm.Quote_Status In ('GEN', 'PEN', 'NAC', 'ACC')
                     And Qm.Quote_Status In
                         ('GEN', 'PEN', 'NAC', 'ACC', 'APP', 'REJ')*/
                      LOOP
          INSERT INTO QMS_QUOTES_UPDATED
            (Quote_Id,
             New_Buycharge_Id,
             New_Lane_No,
             New_Version_No,    --@@Added for the WPBN issues-146448,146968 on 19/12/08
             Sell_Buy_Flag,
             Changedesc,
             Old_Buycharge_Id,
             Old_Lane_No,
             Old_Version_No)
          VALUES
            (i.Quote_Id,
             p_New_Buycharge_Id,
             p_New_Lane_No,
             p_New_Version_No, --@@Added for the WPBN issues-146448,146968 on 19/12/08
             p_Sell_Buy_Flag,
             p_Changedesc,
             p_Old_Buycharge_Id,
             p_Old_Lane_No,
             p_Old_Version_No);
        END LOOP;
      EXCEPTION
        WHEN OTHERS THEN
          g_Err      := '<< ' || SQLERRM || ' >>';
          g_Err_Code := '<< ' || SQLCODE || ' >>';
          INSERT INTO QMS_OBJECTS_ERRORS
            (Ex_Date, Module_Name, Errorcode, Errormessage)
          VALUES
            (SYSDATE, 'QMS_QUOTE_PACK->QUOTE_UPDATED', g_Err_Code, g_Err);
          COMMIT;
      END;
    ELSIF p_Sell_Buy_Flag = 'RSR' THEN
      /*Delete qms_quotes_updated qr
      where qr.sell_buy_flag = p_sell_buy_flag
        and qr.new_buycharge_id = p_Old_buycharge_id
        and qr.new_sellcharge_id = p_Old_sellcharge_id
        and qr.new_lane_no = p_Old_lane_no;*/
      FOR i IN (SELECT Qr.Quote_Id,
                       Qr.Sell_Buy_Flag,
                       Qr.New_Buycharge_Id,
                       Qr.New_Lane_No
                  FROM QMS_QUOTES_UPDATED Qr
                 WHERE Qr.Sell_Buy_Flag = p_Sell_Buy_Flag
                   AND Qr.New_Buycharge_Id = p_Old_Buycharge_Id
                   AND Qr.New_Sellcharge_Id = p_Old_Sellcharge_Id
                   AND Qr.New_Lane_No = p_Old_Lane_No
                   AND Confirm_Flag IS NULL) LOOP
        UPDATE QMS_QUOTES_UPDATED Qu
           SET New_Buycharge_Id     = p_New_Buycharge_Id,
               Qu.New_Sellcharge_Id = p_New_Sellcharge_Id,
               New_Version_No   = p_New_Version_No,--@@Added for the WPBN issues -146448,146968 on 19/12/08
               New_Lane_No          = p_New_Lane_No,
               Changedesc           = p_Changedesc
         WHERE Qu.Quote_Id = i.Quote_Id
           AND Qu.Sell_Buy_Flag = p_Sell_Buy_Flag
           AND Qu.New_Buycharge_Id = p_Old_Buycharge_Id
           AND Qu.New_Sellcharge_Id = p_Old_Sellcharge_Id
           AND Qu.New_Lane_No = p_Old_Lane_No
           AND Confirm_Flag IS NULL;
      END LOOP;
      FOR i IN (SELECT  DISTINCT (Qr.Quote_Id),              --@@Modified by subrahmanyam for the Updated ReportIssue.
                         qr.sellrate_id,
                         qr.version_no
                  FROM QMS_QUOTE_RATES Qr, QMS_QUOTE_MASTER Qm
                 WHERE Qr.Sell_Buy_Flag = 'RSR'
                   /*And Nvl(Qr.Margin_Discount_Flag, 'M') = 'D'*/   --@@Commented by Kameswari for the WPBN issue-140462
                   AND Qr.Buyrate_Id = p_Old_Buycharge_Id
                   --And Qr.Sellrate_Id = p_Old_Sellcharge_Id
                   AND Qr.Rate_Lane_No = p_Old_Lane_No
                  /* And Qr.Line_No='0'*/
                   AND Qr.Quote_Id = Qm.Id
                   AND  NOT EXISTS (SELECT 'x' FROM QMS_QUOTES_UPDATED WHERE quote_id=qr.quote_id AND sell_buy_flag='RSR' AND confirm_flag IS NULL)

                    AND Qm.Active_Flag ='A')
                                       /*And Qm.Id = (Select Id
                                  From Qms_Quote_Master
                                 Where Quote_Id = Qm.Quote_Id
                                   And Version_No In
                                       (Select Max(Version_No)
                                          From Qms_Quote_Master
                                         Where Id = Qr.Quote_Id))
                   And Qm.Complete_Flag = 'C'
                      And Qm.Quote_Status In ('GEN', 'PEN', 'NAC', 'ACC')
                   And Qm.Quote_Status In
                       ('GEN', 'PEN', 'NAC', 'ACC', 'APP', 'REJ')*/
                   LOOP
        INSERT INTO QMS_QUOTES_UPDATED
          (Quote_Id,
           New_Sellcharge_Id,
           New_Buycharge_Id,
           New_Lane_No,
            New_Version_No,--@@Added for the WPBN issues -146448,146968 on 19/12/08
            Sell_Buy_Flag,
           Changedesc,
           Old_Sellcharge_Id,
           Old_Buycharge_Id,
           Old_Lane_No,
           Old_Version_No)
        VALUES
          (i.Quote_Id,
           p_New_Sellcharge_Id,
           p_New_Buycharge_Id,
           p_New_Lane_No,
           p_New_Version_No,--@@Added for the WPBN issues -146448,146968 on 19/12/08
           p_Sell_Buy_Flag,
           p_Changedesc,
           --p_Old_Sellcharge_Id, --@@ Commented by subrahmanyam for the updatedReport Issue
           i.sellrate_id,         --@@ Added by subrahmanyam for the updatedReport Issue
           p_Old_Buycharge_Id,
           p_Old_Lane_No,
           --p_Old_Version_No --@@ Commented by subrahmanyam for the updatedReport Issue
           i.version_no      --@@ Added by subrahmanyam for the updatedReport Issue
	   );
      END LOOP;
    ELSIF p_Sell_Buy_Flag = 'CSR' THEN
      /*Delete qms_quotes_updated qr
      where qr.sell_buy_flag = p_sell_buy_flag
        and qr.new_buycharge_id = p_Old_buycharge_id
        and qr.new_sellcharge_id = p_Old_sellcharge_id
        and qr.new_lane_no = p_Old_lane_no;*/
      FOR i IN (SELECT Qr.Quote_Id,
                       Qr.Sell_Buy_Flag,
                       Qr.New_Buycharge_Id,
                       Qr.New_Lane_No
                  FROM QMS_QUOTES_UPDATED Qr
                 WHERE Qr.Sell_Buy_Flag = p_Sell_Buy_Flag
                   AND Qr.New_Buycharge_Id = p_Old_Buycharge_Id
                   AND Qr.New_Sellcharge_Id = p_Old_Sellcharge_Id
                   AND Qr.New_Lane_No = p_Old_Lane_No
                   AND Confirm_Flag IS NULL) LOOP
        UPDATE QMS_QUOTES_UPDATED Qu
           SET New_Buycharge_Id     = p_New_Buycharge_Id,
               Qu.New_Sellcharge_Id = p_New_Sellcharge_Id,
               New_Lane_No          = p_New_Lane_No,
                New_Version_No   = p_New_Version_No,--@@Added for the WPBN issues -146448,146968 on 19/12/08
             Changedesc           = p_Changedesc
         WHERE Qu.Quote_Id = i.Quote_Id
           AND Qu.Sell_Buy_Flag = p_Sell_Buy_Flag
           AND Qu.New_Buycharge_Id = p_Old_Buycharge_Id
           AND Qu.New_Sellcharge_Id = p_Old_Sellcharge_Id
           AND Qu.New_Lane_No = p_Old_Lane_No
           AND Confirm_Flag IS NULL;
      END LOOP;
      FOR i IN (SELECT DISTINCT (Qr.Quote_Id)
                  FROM QMS_QUOTE_RATES Qr, QMS_QUOTE_MASTER Qm
                 WHERE Qr.Sell_Buy_Flag = 'CSR'
                   /*And Nvl(Qr.Margin_Discount_Flag, 'M') = 'D'*/ --@@Commented by Kameswari for the WPBN issue-140462
                   AND Qr.Buyrate_Id = p_Old_Buycharge_Id
                   AND Qr.Sellrate_Id = p_Old_Sellcharge_Id
                   AND Qr.Rate_Lane_No = p_Old_Lane_No
                  /* And Qr.Line_No='0'*/
                   AND Qr.Quote_Id = Qm.Id
                    AND Qm.Active_Flag ='A')
                   /*And Qm.Id = (Select Id
                                  From Qms_Quote_Master
                                 Where Quote_Id = Qm.Quote_Id
                                   And Version_No In
                                       (Select Max(Version_No)
                                          From Qms_Quote_Master
                                         Where Id = Qr.Quote_Id))
                   And Qm.Complete_Flag = 'C'
                      And Qm.Quote_Status In ('GEN', 'PEN', 'NAC', 'ACC')
                   And Qm.Quote_Status In
                       ('GEN', 'PEN', 'NAC', 'ACC', 'APP', 'REJ')*/
                   LOOP
        INSERT INTO QMS_QUOTES_UPDATED
          (Quote_Id,
           New_Sellcharge_Id,
           New_Buycharge_Id,
           New_Lane_No,
            New_Version_No,--@@Added for the WPBN issues -146448,146968 on 19/12/08
            Sell_Buy_Flag,
           Changedesc,
           Old_Sellcharge_Id,
           Old_Buycharge_Id,
           Old_Lane_No,
           Old_Version_no)
        VALUES
          (i.Quote_Id,
           p_New_Sellcharge_Id,
           p_New_Buycharge_Id,
           p_New_Lane_No,
           p_New_Version_No,--@@Added for the WPBN issues -146448,146968 on 19/12/08
           p_Sell_Buy_Flag,
           p_Changedesc,
           p_Old_Sellcharge_Id,
           p_Old_Buycharge_Id,
           p_Old_Lane_No,
           p_Old_Version_No);
      END LOOP;
    ELSIF p_Sell_Buy_Flag = 'B' THEN
      /* Delete qms_quotes_updated qr
         where qr.sell_buy_flag = p_sell_buy_flag
           and qr.new_buycharge_id = p_Old_buycharge_id;
      */
      FOR i IN (SELECT Qr.Quote_Id, Qr.Sell_Buy_Flag, Qr.New_Buycharge_Id
                  FROM QMS_QUOTES_UPDATED Qr
                 WHERE Qr.Sell_Buy_Flag = p_Sell_Buy_Flag
                   AND Qr.New_Buycharge_Id = p_Old_Buycharge_Id
                   AND Confirm_Flag IS NULL) LOOP
        UPDATE QMS_QUOTES_UPDATED Qu
           SET New_Buycharge_Id = p_New_Buycharge_Id,
               Changedesc       = p_Changedesc
         WHERE Qu.Quote_Id = i.Quote_Id
           AND Qu.Sell_Buy_Flag = p_Sell_Buy_Flag
           AND Qu.New_Buycharge_Id = p_Old_Buycharge_Id
           AND Confirm_Flag IS NULL;
      END LOOP;
      FOR i IN (SELECT  DISTINCT (Qr.Quote_Id)
                  FROM QMS_QUOTE_RATES Qr, QMS_QUOTE_MASTER Qm
                 WHERE (Qr.Sell_Buy_Flag = p_Sell_Buy_Flag /*Or
                       (Qr.Sell_Buy_Flag = 'S' And
                       Nvl(Qr.Margin_Discount_Flag, 'M') = 'M')*/)
                   AND Qr.Buyrate_Id = p_Old_Buycharge_Id
                   AND Qr.Quote_Id = Qm.Id
                  /* And Qr.Line_No='0'*/
                   AND Qm.Active_Flag ='A')
                 /*  And Qm.Id = (Select Id
                                  From Qms_Quote_Master
                                 Where Quote_Id = Qm.Quote_Id
                                   And Version_No In
                                       (Select Max(Version_No)
                                          From Qms_Quote_Master
                                         Where Id = Qr.Quote_Id))
                   And Qm.Complete_Flag = 'C'
                     And Qm.Quote_Status In ('GEN', 'PEN', 'NAC', 'ACC')
                   And Qm.Quote_Status In
                       ('GEN', 'PEN', 'NAC', 'ACC', 'APP', 'REJ')*/
                    LOOP
        INSERT INTO QMS_QUOTES_UPDATED
          (Quote_Id,
           New_Buycharge_Id,
           Sell_Buy_Flag,
           Changedesc,
           Old_Buycharge_Id)
        VALUES
          (i.Quote_Id,
           p_New_Buycharge_Id,
           p_Sell_Buy_Flag,
           p_Changedesc,
           p_Old_Buycharge_Id);
      END LOOP;
    ELSIF p_Sell_Buy_Flag = 'S' THEN
      /* Delete qms_quotes_updated qr
         where qr.sell_buy_flag = p_sell_buy_flag
           and qr.new_buycharge_id = p_Old_buycharge_id
           and qr.new_sellcharge_id = p_Old_sellcharge_id;
      */
      FOR i IN (SELECT Qr.Quote_Id, Qr.Sell_Buy_Flag, Qr.New_Buycharge_Id
                  FROM QMS_QUOTES_UPDATED Qr
                 WHERE Qr.Sell_Buy_Flag = p_Sell_Buy_Flag
                   AND Qr.New_Buycharge_Id = p_Old_Buycharge_Id
                   AND Qr.New_Sellcharge_Id = p_Old_Sellcharge_Id
                   AND Confirm_Flag IS NULL) LOOP
        UPDATE QMS_QUOTES_UPDATED Qu
           SET New_Buycharge_Id     = p_New_Buycharge_Id,
               Qu.New_Sellcharge_Id = p_New_Sellcharge_Id,
               Changedesc           = p_Changedesc
         WHERE Qu.Quote_Id = i.Quote_Id
           AND Qu.Sell_Buy_Flag = p_Sell_Buy_Flag
           AND Qu.New_Buycharge_Id = p_Old_Buycharge_Id
           AND Qu.New_Sellcharge_Id = p_Old_Sellcharge_Id
           AND Confirm_Flag IS NULL;
      END LOOP;
      FOR i IN (SELECT  DISTINCT (Qr.Quote_Id)
                  FROM QMS_QUOTE_RATES Qr, QMS_QUOTE_MASTER Qm
                 WHERE Qr.Sell_Buy_Flag = 'S'
                   /*And Nvl(Qr.Margin_Discount_Flag, 'M') = 'D'*/--@@Commented by Kameswari for the WPBN issue-140462
                   AND Qr.Buyrate_Id = p_Old_Buycharge_Id
                   AND Qr.Sellrate_Id = p_Old_Sellcharge_Id
                   AND Qr.Quote_Id = Qm.Id
                   /*And Qr.line_no='0'*/
                   AND Qm.Active_Flag ='A')
                   /*And Qm.Id = (Select Id
                                  From Qms_Quote_Master
                                 Where Quote_Id = Qm.Quote_Id
                                   And Version_No In
                                       (Select Max(Version_No)
                                          From Qms_Quote_Master
                                         Where Id = Qr.Quote_Id))*/
                /*   And Qm.Complete_Flag = 'C'*/
                      --And Qm.Quote_Status In ('GEN', 'PEN', 'NAC', 'ACC')
                 /*  And Qm.Quote_Status In
                       ('GEN', 'PEN', 'NAC', 'ACC', 'APP', 'REJ')
            */        LOOP
        INSERT INTO QMS_QUOTES_UPDATED
          (Quote_Id,
           New_Sellcharge_Id,
           New_Buycharge_Id,
           Sell_Buy_Flag,
           Changedesc,
           Old_Sellcharge_Id,
           Old_Buycharge_Id)
        VALUES
          (i.Quote_Id,
           p_New_Sellcharge_Id,
           p_New_Buycharge_Id,
           p_Sell_Buy_Flag,
           p_Changedesc,
           p_Old_Sellcharge_Id,
           p_Old_Buycharge_Id);
      END LOOP;
    ELSIF p_Sell_Buy_Flag = 'BC' THEN
      /*Delete qms_quotes_updated qr
      where qr.sell_buy_flag = p_sell_buy_flag
        and qr.new_buycharge_id = p_Old_buycharge_id
        and qr.CHARGE_AT = p_charge_type;*/
      FOR i IN (SELECT Qr.Quote_Id,
                       Qr.Sell_Buy_Flag,
                       Qr.New_Buycharge_Id,
                       Qr.Charge_At
                  FROM QMS_QUOTES_UPDATED Qr
                 WHERE Qr.Sell_Buy_Flag = p_Sell_Buy_Flag
                   AND Qr.New_Buycharge_Id = p_Old_Buycharge_Id
                   AND Qr.Charge_At = v_Charge_Type
                   AND Qr.Zone_Code = p_Zone_Code
                   AND Confirm_Flag IS NULL) LOOP
        UPDATE QMS_QUOTES_UPDATED Qu
           SET Qu.New_Buycharge_Id = p_New_Buycharge_Id,
               Changedesc          = v_ship_mode || ' ' || v_Charge_Type ||
                                     ' Charges for ZONE-' || p_Zone_Code ||
                                     ' Of ' || p_Changedesc
         WHERE Qu.Quote_Id = i.Quote_Id
           AND Qu.Sell_Buy_Flag = p_Sell_Buy_Flag
           AND Qu.New_Buycharge_Id = p_Old_Buycharge_Id
           AND Qu.Charge_At = v_Charge_Type
           AND Qu.Zone_Code = p_Zone_Code
           AND Confirm_Flag IS NULL;
      END LOOP;
      FOR i IN (SELECT DISTINCT  (Qr.Quote_Id)
                  FROM QMS_QUOTE_RATES Qr, QMS_QUOTE_MASTER Qm
                 WHERE (Qr.Sell_Buy_Flag = p_Sell_Buy_Flag OR
                       (Qr.Sell_Buy_Flag = 'SC' AND
                       NVL(Qr.Margin_Discount_Flag, 'M') = 'M') )
                   AND Qr.Buyrate_Id = p_Old_Buycharge_Id
                   AND Qr.Charge_At = v_Charge_Type
                   AND DECODE(v_Charge_Type,
                              'Pickup',
                              Qm.Shipperzones,
                              Qm.Consigneezones) = p_Zone_Code
                   AND Qr.Quote_Id = Qm.Id
                  /* And Qr.Line_No='0'*/
                     AND Qm.Active_Flag ='A' )
                  /* And Qm.Id = (Select Id                              --@@Modified by Kameswari on 15/01/08
                                  From Qms_Quote_Master
                                 Where Quote_Id = Qm.Quote_Id
                                   And Version_No In
                                       (Select Max(Version_No)
                                          From Qms_Quote_Master
                                         Where Id = Qr.Quote_Id))
                   And Qm.Complete_Flag = 'C'
                      And Qm.Quote_Status In ('GEN', 'PEN', 'NAC', 'ACC')
                   And Qm.Quote_Status In
                       ('GEN', 'PEN', 'NAC', 'ACC', 'APP', 'REJ')*/
                  LOOP
        INSERT INTO QMS_QUOTES_UPDATED
          (Quote_Id,
           New_Buycharge_Id,
           Sell_Buy_Flag,
           Charge_At,
           Zone_Code,
           Changedesc,
           Old_Buycharge_Id)
        VALUES
          (i.Quote_Id,
           p_New_Buycharge_Id,
           p_Sell_Buy_Flag,
           v_Charge_Type,
           p_Zone_Code,
           v_ship_mode || ' ' || v_Charge_Type || ' Charges for ZONE-' ||
           p_Zone_Code || ' Of ' || p_Changedesc,
           p_Old_Buycharge_Id);
      END LOOP;
    ELSIF p_Sell_Buy_Flag = 'SC' THEN
      /*Delete qms_quotes_updated qr
      where qr.sell_buy_flag = p_sell_buy_flag
        and qr.new_buycharge_id = p_Old_buycharge_id
        and qr.new_sellcharge_id = p_Old_sellcharge_id
        and qr.CHARGE_AT = p_charge_type;*/
      BEGIN
        FOR i IN (SELECT Qr.Quote_Id,
                         Qr.Sell_Buy_Flag,
                         Qr.New_Buycharge_Id,
                         Qr.Charge_At
                    FROM QMS_QUOTES_UPDATED Qr
                   WHERE Qr.Sell_Buy_Flag = p_Sell_Buy_Flag
                     AND Qr.New_Buycharge_Id = p_Old_Buycharge_Id
                     AND Qr.New_Sellcharge_Id = p_Old_Sellcharge_Id
                     AND Qr.Charge_At = v_Charge_Type
                     AND Qr.Zone_Code = p_Zone_Code
                     AND Confirm_Flag IS NULL) LOOP
          UPDATE QMS_QUOTES_UPDATED Qu
             SET New_Buycharge_Id     = p_New_Buycharge_Id,
                 Qu.New_Sellcharge_Id = p_New_Sellcharge_Id,
                 Changedesc           = v_ship_mode || ' ' || v_Charge_Type ||
                                        ' Charges for ZONE-' || p_Zone_Code ||
                                        ' Of ' || p_Changedesc
           WHERE Qu.Quote_Id = i.Quote_Id
             AND Qu.Sell_Buy_Flag = p_Sell_Buy_Flag
             AND Qu.New_Buycharge_Id = p_Old_Buycharge_Id
             AND Qu.New_Sellcharge_Id = p_Old_Sellcharge_Id
             AND Qu.Charge_At = v_Charge_Type
             AND Qu.Zone_Code = p_Zone_Code
             AND Confirm_Flag IS NULL;
        END LOOP;

        FOR i IN (SELECT DISTINCT (Qr.Quote_Id)
                    FROM QMS_QUOTE_RATES Qr, QMS_QUOTE_MASTER Qm
                   WHERE Qr.Sell_Buy_Flag = 'SC'
                     /*And Nvl(Qr.Margin_Discount_Flag, 'M') = 'D'*/--@@Commented by Kameswari for the WPBN issue-140462
                     AND Qr.Buyrate_Id = p_Old_Buycharge_Id
                     AND Qr.Sellrate_Id = p_Old_Sellcharge_Id
                     AND Qr.Charge_At = v_Charge_Type
                     AND DECODE(v_Charge_Type,
                                'Pickup',
                                Qm.Shipperzones,
                                Qm.Consigneezones) = p_Zone_Code
                     AND Qr.Quote_Id = Qm.Id
                   /*  And Qr.Line_No='0'   */
                     AND Qm.Active_Flag = 'A' )                        --@@Modified by Kameswari
                    /* And Qm.Id = (Select Id
                                    From Qms_Quote_Master
                                   Where Quote_Id = Qm.Quote_Id
                                     And Version_No In
                                         (Select Max(Version_No)
                                            From Qms_Quote_Master
                                           Where Id = Qr.Quote_Id))
                     And Qm.Complete_Flag = 'C'
                        And Qm.Quote_Status In ('GEN', 'PEN', 'NAC', 'ACC')
                     And Qm.Quote_Status In
                         ('GEN', 'PEN', 'NAC', 'ACC', 'APP', 'REJ')*/
                      LOOP
          INSERT INTO QMS_QUOTES_UPDATED
            (Quote_Id,
             New_Sellcharge_Id,
             New_Buycharge_Id,
             Sell_Buy_Flag,
             Charge_At,
             Zone_Code,
             Changedesc,
             Old_Sellcharge_Id,
             Old_Buycharge_Id)
          VALUES
            (i.Quote_Id,
             p_New_Sellcharge_Id,
             p_New_Buycharge_Id,
             p_Sell_Buy_Flag,
             v_Charge_Type,
             p_Zone_Code,
             v_ship_mode || ' ' || v_Charge_Type || ' Charges for ZONE-' ||
             p_Zone_Code || ' Of ' || p_Changedesc,
             p_Old_Sellcharge_Id,
             p_Old_Buycharge_Id);
        END LOOP;
        /*EXCEPTION
        WHEN OTHERS
        THEN
           g_err := '<< ' || SQLERRM || ' >>';
           g_err_code := '<< ' || SQLCODE || ' >>';

           INSERT INTO qms_objects_errors
                       (ex_date,
                        module_name,
                        errorcode, errormessage
                       )
                VALUES (SYSDATE,
                        'QMS_QUOTE_PACK->QUOTE_update_PROC',
                        g_err_code, g_err
                       );*/
      END;
    END IF;
  END;

  /*PROCEDURE qms_updated_quotes_count (
     p_terminalid         VARCHAR2,
     p_rs           OUT   resultset
  )
  AS
     v_opr_adm_flag   VARCHAR2 (10);
     \*v_quote_id       NUMBER (16);*\
     v_terminals      VARCHAR2 (3000);

     CURSOR c1
     IS
        (SELECT p_terminalid term_id
           FROM DUAL
         UNION
         SELECT     child_terminal_id term_id
               FROM fs_fr_terminal_regn
         CONNECT BY PRIOR child_terminal_id = parent_terminal_id
         START WITH parent_terminal_id = p_terminalid);

     CURSOR c2
     IS
        (SELECT DISTINCT terminalid term_id
                    FROM fs_fr_terminalmaster);
  BEGIN
     SELECT oper_admin_flag
       INTO v_opr_adm_flag
       FROM fs_fr_terminalmaster
      WHERE terminalid = p_terminalid;

     IF v_opr_adm_flag <> 'H'
     THEN
        FOR i IN c1
        LOOP
           v_terminals := v_terminals || '''' || i.term_id || '''' || ',';
        END LOOP;
     ELSE
        FOR i IN c2
        LOOP
           v_terminals := v_terminals || '''' || i.term_id || '''' || ',';
        END LOOP;
     END IF;

     v_terminals := SUBSTR (v_terminals, 2, LENGTH (v_terminals) - 3);

     OPEN p_rs
      FOR    'SELECT DISTINCT QU.CHANGEDESC,
                        (SELECT COUNT(*) FROM QMS_QUOTES_UPDATED A WHERE QM.ID=A.QUOTE_ID
                           AND A.CHANGEDESC=QU.CHANGEDESC) UPDATED_QUOTES,
                        (SELECT COUNT(*) FROM QMS_QUOTES_UPDATED A WHERE QM.ID=A.QUOTE_ID
                           AND A.CHANGEDESC=QU.CHANGEDESC AND A.CONFIRM_FLAG=''C'') CONFIRMED_QUOTES
                         FROM QMS_QUOTES_UPDATED QU,QMS_QUOTE_MASTER QM WHERE QM.ID=QU.QUOTE_ID
                          AND QM.TERMINAL_ID IN ('
          || ''''
          || v_terminals
          || ''''
          || ') AND (CHANGEDESC IS NOT NULL OR LENGTH(TRIM(CHANGEDESC))!=0)';
  END qms_updated_quotes_count;

  PROCEDURE qms_updated_quotes_info (
     p_change_desc         VARCHAR2,
     p_terminalid          VARCHAR2,
     p_rs            OUT   resultset
  )
  AS
     v_opr_adm_flag   VARCHAR2 (10);
     v_terminals      VARCHAR2 (3000);

     CURSOR c1
     IS
        (SELECT p_terminalid term_id
           FROM DUAL
         UNION
         SELECT     child_terminal_id term_id
               FROM fs_fr_terminal_regn
         CONNECT BY PRIOR child_terminal_id = parent_terminal_id
         START WITH parent_terminal_id = p_terminalid);

     CURSOR c2
     IS
        (SELECT DISTINCT terminalid term_id
                    FROM fs_fr_terminalmaster);
  BEGIN
     SELECT oper_admin_flag
       INTO v_opr_adm_flag
       FROM fs_fr_terminalmaster
      WHERE terminalid = p_terminalid;

     IF v_opr_adm_flag <> 'H'
     THEN
        FOR i IN c1
        LOOP
           v_terminals := v_terminals || '''' || i.term_id || '''' || ',';
        END LOOP;
     ELSE
        FOR i IN c2
        LOOP
           v_terminals := v_terminals || '''' || i.term_id || '''' || ',';
        END LOOP;
     END IF;

     v_terminals := SUBSTR (v_terminals, 2, LENGTH (v_terminals) - 3);

     OPEN p_rs
      FOR    'SELECT QM.IU_FLAG, QM.CUSTOMER_ID, QM.QUOTE_ID,
                   DECODE((SELECT COUNT(*)
                             FROM FS_RT_LEG LG, FS_RT_PLAN RP
                            WHERE RP.QUOTE_ID=QM.QUOTE_ID
                              AND LG.RT_PLAN_ID=RP.RT_PLAN_ID),
                            1, QM.SHIPMENT_MODE, 100) SMODE,
                   DECODE((SELECT COUNT(*)
                             FROM FS_RT_LEG LG, FS_RT_PLAN RP
                            WHERE RP.QUOTE_ID=QM.QUOTE_ID
                            AND LG.RT_PLAN_ID=RP.RT_PLAN_ID),
                            1,(SELECT DISTINCT SERVICE_LEVEL
                                 FROM QMS_BUYRATES_DTL BD,QMS_QUOTE_RATES QR
                                WHERE BD.BUYRATEID=QR.BUYRATE_ID
                                AND QR.QUOTE_ID=QM.ID
                                AND QR.SELL_BUY_FLAG IN(''BR'',''RSR'',''CSR'')), ''Multi-Modal'') SERVICE,
                   QM.ORIGIN_LOCATION,
                   (SELECT COUNTRYID
                     FROM FS_FR_LOCATIONMASTER
                    WHERE LOCATIONID=QM.ORIGIN_LOCATION) ORIGIN_COUNTRY,
                   QM.DEST_LOCATION,
                   (SELECT COUNTRYID
                     FROM FS_FR_LOCATIONMASTER
                    WHERE LOCATIONID=QM.DEST_LOCATION) DEST_COUNTRY
                     FROM QMS_QUOTE_MASTER QM
                    WHERE ID IN (SELECT DISTINCT(QU.QUOTE_ID)
                                   FROM QMS_QUOTES_UPDATED QU, QMS_QUOTE_MASTER QM
                                  WHERE QU.CHANGEDESC='
          || ''''
          || p_change_desc
          || ''''
          || '
                                  AND QU.QUOTE_ID=QM.ID
                                  AND QU.CONFIRM_FLAG  IS NULL
                                  AND QM.TERMINAL_ID IN ('
          || ''''
          || v_terminals
          || ''''
          || '))';
  END qms_updated_quotes_info;*/
  /*
     This procedure is called when the user selects a quote to be confirmed by checking the checkbox
     It creates a new quote with the updated rates/charges/cartages and inactivates the old one.

     The IN parameters are
         p_quoteid             NUMBER,
        p_modifiedby          VARCHAR2,
        p_sellbuyflag         VARCHAR2,
        p_changedesc          VARCHAR2

     The OUT Parameters are
        p_newquoteid    OUT   NUMBER,
        p_newuniqueid   OUT   NUMBER
  */
  PROCEDURE Qms_Update_Quote(p_Quoteid     NUMBER,
                             p_Modifiedby  VARCHAR2,
                             p_Sellbuyflag VARCHAR2,
                             p_Changedesc  VARCHAR2,
                             p_Newquoteid  VARCHAR2,
                             p_Newuniqueid OUT NUMBER) AS
    v_Sql         VARCHAR2(1000) := '';
    v_Shmode      VARCHAR2(5);
    v_Weightbreak VARCHAR2(10);
    /*v_salesperson        VARCHAR2 (50);*/
    v_Servicelevel       VARCHAR2(15);
    v_Marginid           VARCHAR2(10);
    v_Marginforfreight   NUMBER(8, 2);
    v_Marginforcharges   NUMBER(8, 2);
    v_Marginforcartage   NUMBER(8, 2);
    v_Discountforfreight NUMBER(8, 2);
    v_Discountforcharges NUMBER(8, 2);
    v_Discountforcartage NUMBER(8, 2);
    v_Rt_Plan_Id         NUMBER(16);
    v_lno             NUMBER(10);
    v_newLineno          NUMBER(10);
    COUNT                NUMBER(10);
    v_oldchargebasis      VARCHAR2(15); --@@Added by Kameswari for the WPBN issue-154398 on 19/02/09
    v_oldwtbreak      VARCHAR2(15);
    v_newchargebasis      VARCHAR2(15);
    v_newwtbreak      VARCHAR2(15);
    v_newchargeid      VARCHAR2(15);
    v_oldchargeid      VARCHAR2(15);--@@WPBN issue-154398
    v_cType            VARCHAR2(15);
    v_Frequency_Checked VARCHAR2(15);
    v_Carrier_Checked VARCHAR2(15);
    v_Transit_Checked VARCHAR2(15);
    v_Validity_Checked VARCHAR2(15);
    V_charge_description  VARCHAR2(235); --kishore
    v_charge_at           VARCHAR2(15);
    v_serial_no         NUMBER(16);
    V_Margin_Discount_Flag VARCHAR2(1);
    V_Margin_Type          VARCHAR2(1);
    V_Margin               NUMBER(20,5);
    V_Discount_type        VARCHAR2(1);
    V_Discount             NUMBER(20,5);
    v_count               NUMBER(10);
    V_SLAB_COUNT          NUMBER(10);
    --KISHORE
    V_ISMULTIQUOTE        VARCHAR2(2);
    p_Newuniqueid1        NUMBER(10);


  BEGIN
    IF (UPPER(p_Sellbuyflag) = 'BR') THEN
        SELECT QMS.SHIPMENT_MODE INTO v_Shmode FROM QMS_QUOTE_MASTER QMS WHERE QMS.ID = p_Quoteid;
/*      SELECT DISTINCT Rl.Shpmnt_Mode     @@@2@@COMMENTED BY GOVIND FOR GETTING  THE shipment mode from quote master
        INTO v_Shmode
        FROM FS_RT_LEG Rl, FS_RT_PLAN Rp, QMS_QUOTES_UPDATED Qu
       WHERE Rl.Rt_Plan_Id = Rp.Rt_Plan_Id
         AND Rl.Serial_No =
             (SELECT DISTINCT Iqr.Serial_No
                FROM QMS_QUOTE_RATES Iqr
               WHERE Iqr.Quote_Id = Qu.Quote_Id
                 AND DECODE(Iqr.Sell_Buy_Flag,
                            'RSR',
                            DECODE(Iqr.Margin_Discount_Flag, 'M', 'BR'),
                            'CSR',
                            DECODE(Iqr.Margin_Discount_Flag, 'M', 'BR'),
                            Iqr.Sell_Buy_Flag) = Qu.Sell_Buy_Flag

                 AND Iqr.Buyrate_Id = Qu.Old_Buycharge_Id
                 AND Iqr.Rate_Lane_No = Qu.Old_Lane_No
                 AND iqr.serial_no IS NOT NULL --kishore
                 )
         AND Rp.Quote_Id =
             (SELECT Quote_Id FROM QMS_QUOTE_MASTER WHERE Id = Qu.Quote_Id)
         AND Qu.Quote_Id = p_Quoteid
         AND Qu.Changedesc = p_Changedesc;
*/      SELECT DISTINCT Bm.Weight_Break, Bd.Service_Level
        INTO v_Weightbreak, v_Servicelevel
        FROM QMS_BUYRATES_MASTER Bm,
             QMS_BUYRATES_DTL    Bd,
             QMS_QUOTES_UPDATED  Qu
       WHERE Bm.Buyrateid = Bd.Buyrateid
         AND Bd.Buyrateid = Qu.Old_Buycharge_Id
         AND Bd.Lane_No = Qu.Old_Lane_No
         AND Qu.Sell_Buy_Flag = p_Sellbuyflag
         AND Qu.Quote_Id = p_Quoteid
         AND Qu.Changedesc = p_Changedesc
         AND Bd.Line_No = '0';
    ELSIF (UPPER(p_Sellbuyflag) = 'RSR' OR UPPER(p_Sellbuyflag) = 'CSR') THEN
     SELECT QMS.SHIPMENT_MODE INTO v_Shmode FROM QMS_QUOTE_MASTER QMS WHERE QMS.ID = p_Quoteid;
      /*SELECT DISTINCT Rl.Shpmnt_Mode   @@@2@@COMMENTED BY GOVIND FOR GETTING  THE shipment mode from quote master
        INTO v_Shmode
        FROM FS_RT_LEG Rl, FS_RT_PLAN Rp, QMS_QUOTES_UPDATED Qu
       WHERE Rl.Rt_Plan_Id = Rp.Rt_Plan_Id
         AND Rl.Serial_No =
             (SELECT DISTINCT Iqr.Serial_No
                FROM QMS_QUOTE_RATES Iqr
               WHERE Iqr.Quote_Id = Qu.Quote_Id
                 AND Iqr.Sell_Buy_Flag = Qu.Sell_Buy_Flag
                 AND Iqr.Sellrate_Id = Qu.Old_Sellcharge_Id
                 AND Iqr.Buyrate_Id = Qu.Old_Buycharge_Id
                 AND Iqr.Rate_Lane_No = Qu.Old_Lane_No)
         AND Rp.Quote_Id =
             (SELECT Quote_Id FROM QMS_QUOTE_MASTER WHERE Id = Qu.Quote_Id)
         AND Qu.Quote_Id = p_Quoteid
         AND Qu.Changedesc = p_Changedesc;*/
      SELECT DISTINCT Sm.Weight_Break, Sd.Servicelevel_Id
        INTO v_Weightbreak, v_Servicelevel
        FROM QMS_REC_CON_SELLRATESMASTER Sm,
             QMS_REC_CON_SELLRATESDTL    Sd,
             QMS_QUOTES_UPDATED          Qu
       WHERE Sm.Rec_Con_Id = Sd.Rec_Con_Id
         AND Sd.Rec_Con_Id = Qu.Old_Sellcharge_Id
         AND Sd.Buyrateid = Qu.Old_Buycharge_Id
         AND Sd.Lane_No = Qu.Old_Lane_No
         AND Sd.Line_No = '0'
         AND Qu.Sell_Buy_Flag = p_Sellbuyflag
         AND Qu.Quote_Id = p_Quoteid
         AND Qu.Changedesc = p_Changedesc;
    END IF;
    IF (UPPER(p_Sellbuyflag) = 'BR' OR UPPER(p_Sellbuyflag) = 'RSR' OR
       UPPER(p_Sellbuyflag) = 'CSR') THEN
      IF (v_Shmode = '1') THEN
        v_Marginid := '1';
      ELSIF (v_Shmode = '2' AND v_Weightbreak = 'LIST') THEN
        v_Marginid := '4';
      ELSIF (v_Shmode = '2' AND v_Weightbreak <> 'LIST') THEN
        v_Marginid := '2';
      ELSIF (v_Shmode = '4' AND v_Weightbreak = 'LIST') THEN
        v_Marginid := '15';
      ELSIF (v_Shmode = '4' AND v_Weightbreak <> 'LIST') THEN
        v_Marginid := '7';
      END IF;
      SELECT Minmargins, Maxdiscount
        INTO v_Marginforfreight, v_Discountforfreight
        FROM QMS_MARGIN_LIMIT_DTL
       WHERE Invalidate = 'F'
         AND Margin_Id = v_Marginid
         AND Service_Level = v_Servicelevel
         AND Chargetype = 'FREIGHT'
         AND Levelno =
             (SELECT Level_No
                FROM QMS_DESIGNATION
               WHERE Designation_Id =
                     (SELECT Designation_Id
                        FROM FS_USERMASTER
                       WHERE Empid = (SELECT Sales_Person
                                        FROM QMS_QUOTE_MASTER
                                       WHERE Id = p_Quoteid)));
    ELSIF (UPPER(p_Sellbuyflag) = 'B' OR UPPER(p_Sellbuyflag) = 'S') THEN
      BEGIN
        SELECT Minmargins, Maxdiscount
          INTO v_Marginforcharges, v_Discountforcharges
          FROM QMS_MARGIN_LIMIT_DTL
         WHERE Invalidate = 'F'
           AND Chargetype = 'CHARGES'
           AND Levelno =
               (SELECT Level_No
                  FROM QMS_DESIGNATION
                 WHERE Designation_Id =
                       (SELECT Designation_Id
                          FROM FS_USERMASTER
                         WHERE Empid = (SELECT Sales_Person
                                          FROM QMS_QUOTE_MASTER
                                         WHERE Id = p_Quoteid)));
      EXCEPTION
        WHEN NO_DATA_FOUND THEN
          v_Marginforcharges   := 0;
          v_Discountforcharges := 0;
      END;
    ELSIF (UPPER(p_Sellbuyflag) = 'BC' OR UPPER(p_Sellbuyflag) = 'SC') THEN
      BEGIN
        SELECT Minmargins, Maxdiscount
          INTO v_Marginforcartage, v_Discountforcartage
          FROM QMS_MARGIN_LIMIT_DTL
         WHERE Invalidate = 'F'
           AND Chargetype = 'CARTAGES'
           AND Levelno =
               (SELECT Level_No
                  FROM QMS_DESIGNATION
                 WHERE Designation_Id =
                       (SELECT Designation_Id
                          FROM FS_USERMASTER
                         WHERE Empid = (SELECT Sales_Person
                                          FROM QMS_QUOTE_MASTER
                                         WHERE Id = p_Quoteid)));
      EXCEPTION
        WHEN NO_DATA_FOUND THEN
          v_Marginforcartage   := 0;
          v_Discountforcartage := 0;
      END;
    END IF;
    /*Select Quote_Seq.Nextval Into p_Newquoteid From Dual;*/  /*@@ Commented by subrahmanyam for the enhancement #146971 on 03/12/2008 */
    SELECT Quote_Master_Seq.NEXTVAL INTO p_Newuniqueid FROM Dual;
    INSERT INTO QMS_QUOTE_MASTER
      (Quote_Id,
       Shipment_Mode,
       Prequote_Id,
       Iu_Flag,
       Effective_Date,
       Valid_To,
       Accept_Validityperiod,
       Customer_Id,
       Customer_Addressid,
       Created_Date,
       Created_By,
       Sales_Person,
       Industry_Id,
       Commodity_Id,
       Hazardous_Ind,
       Un_Number,
       CLASS,
       Service_Level_Id,
       Inco_Terms_Id,
       Quoting_Station,
       Origin_Location,
       Shipper_Zipcode,
       Origin_Port,
       Overlength_Cargonotes,
       Routing_Id,
       Dest_Location,
       Consignee_Zipcode,
       Destionation_Port,
       Spot_Rates_Flag,
       Spot_Rate_Type,
       Email_Flag,
       Fax_Flag,
       Print_Flag,
       Escalated_To,
       Modified_Date,--
       Modified_By,
       Terminal_Id,
       Version_No,
       Basis,
       Shipperzones,
       Consigneezones,
       Id,
       Pn_Flag,
       Update_Flag,
       Active_Flag,
       Sent_Flag,
       Complete_Flag,
       Quote_Status,
       Spotbuyrateid,
       Escalation_Flag,
       Ie_Flag,
       Created_Tstmp,--
       Expired_Flag,
       App_Rej_Tstmp,
       Cargo_Acc_Type,
       Cargo_Acc_Place,
       SHIPPER_MODE,
       CONSIGNEE_MODE,
       SHIPPER_CONSOLE_TYPE,
       CONSIGNEE_CONSOLE_TYPE,
       MULTI_QUOTE_LANE_NO,
       MULTI_QUOTE_CARRIER_ID,
       MULTI_QUOTE_SERVICE_LEVEL,
       IS_MULTI_QUOTE,
       MULTI_QUOTE_WEIGHT_BREAK,
       multi_quote_with,
       multi_lane_order ,
       cust_requested_date ,
       cust_requested_time,
       status_reason)
      SELECT p_Newquoteid,
             Shipment_Mode,
             Prequote_Id,
             Iu_Flag,
             --Effective_Date, @@Modified by Kiran for the WPBN issue - on 28/07/2011
             sysdate,
             --Valid_To,
             decode(valid_to, null, null, sysdate + 30),--@@Modified by Kiran for the WPBN issue - on 28/07/2011
             Accept_Validityperiod,
             Customer_Id,
             Customer_Addressid,
             Created_Date,
             Created_By,
             Sales_Person,
             Industry_Id,
             Commodity_Id,
             Hazardous_Ind,
             Un_Number,
             CLASS,
             Service_Level_Id,
             Inco_Terms_Id,
             Quoting_Station,
             Origin_Location,
             Shipper_Zipcode,
             Origin_Port,
             Overlength_Cargonotes,
             Routing_Id,
             Dest_Location,
             Consignee_Zipcode,
             Destionation_Port,
             Spot_Rates_Flag,
             Spot_Rate_Type,
             Email_Flag,
             Fax_Flag,
             Print_Flag,
             Escalated_To,
             SYSDATE,
             p_Modifiedby,
             Terminal_Id,
             1,
             Basis,
             Shipperzones,
             Consigneezones,
             p_Newuniqueid,
             Pn_Flag,
             p_Sellbuyflag,
             'A',
             'U',
             Complete_Flag,
             --'GEN',
             Quote_Status, --@@Modified by Kameswari for the WPBN Issue-116548
             Spotbuyrateid,
             Escalation_Flag,
             Ie_Flag,
             SYSDATE,
             Expired_Flag,
             App_Rej_Tstmp,
             Cargo_Acc_Type,
             Cargo_Acc_Place,
             Shipper_Mode,
             Consignee_Mode,
             Shipper_Console_Type,
             Consignee_Console_Type,
             MULTI_QUOTE_LANE_NO,
             MULTI_QUOTE_CARRIER_ID,
             MULTI_QUOTE_SERVICE_LEVEL,
             IS_MULTI_QUOTE,
             MULTI_QUOTE_WEIGHT_BREAK,
             multi_quote_with,
             multi_lane_order ,
             cust_requested_date ,
             cust_requested_time,
             status_reason
        FROM QMS_QUOTE_MASTER
       WHERE Id = p_Quoteid;

    INSERT INTO QMS_QUOTE_CONTACTDTL
      (Quote_Id, Contact_Name, Id, Customerid, Sl_No)
      SELECT p_Newuniqueid,
             Contact_Name,
             Seq_Quote_Contactdtl_Id.NEXTVAL,
             Customerid,
             Sl_No
        FROM QMS_QUOTE_CONTACTDTL
       WHERE Quote_Id = p_Quoteid;
    INSERT INTO QMS_QUOTE_SPOTRATES
      (Quote_Id,
       Lane_No,
       Upper_Bound,
       Lower_Bound,
       Charge_Rate,
       Weight_Break_Slab,
       Line_No,
       Shipment_Mode,
       Servicelevel,
       Id,
       Uom,
       Density_Code,
       Weight_Break,
       CurrencyId,
       Rate_description)
      SELECT p_Newuniqueid,
             Lane_No,
             Upper_Bound,
             Lower_Bound,
             Charge_Rate,
             Weight_Break_Slab,
             Line_No,
             Shipment_Mode,
             Servicelevel,
             Seq_Quote_Spotrates.NEXTVAL,
             Uom,
             Density_Code,
             Weight_Break,
             CurrencyId,
             Rate_Description
        FROM QMS_QUOTE_SPOTRATES
       WHERE Quote_Id = p_Quoteid;
    INSERT INTO QMS_QUOTE_CHARGEGROUPDTL
      (Quote_Id, Chargegroupid, Id)
      SELECT p_Newuniqueid, Chargegroupid, Seq_Chargegroupdtl_Id.NEXTVAL
        FROM QMS_QUOTE_CHARGEGROUPDTL
       WHERE Quote_Id = p_Quoteid;
    --@@Added by Kameswari for the WPBN issue-13677
    INSERT INTO QMS_QUOTE_NOTES
      (quote_id, internal_notes, external_notes, id)
      SELECT p_Newuniqueid,
             internal_notes,
             external_notes,
             seq_qms_quote_notes.NEXTVAL
        FROM QMS_QUOTE_NOTES
       WHERE quote_id = p_Quoteid;

    INSERT INTO QMS_QUOTE_ATTACHMENTDTL
      (id, quote_id, attachment_id)
      SELECT seq_quote_attachdtl_id.NEXTVAL, p_Newuniqueid, attachment_id
        FROM QMS_QUOTE_ATTACHMENTDTL
       WHERE quote_id = p_Quoteid;
    --@@WPBN issue-13677
    INSERT INTO QMS_QUOTE_HF_DTL
      (Quote_Id, Header, Content, Clevel, Align, Id)
      SELECT p_Newuniqueid,
             Header,
             Content,
             Clevel,
             Align,
             Seq_Qms_Quote_Hf_Dtl.NEXTVAL
        FROM QMS_QUOTE_HF_DTL
       WHERE Quote_Id = p_Quoteid;

  FOR I IN (SELECT RTP.RT_PLAN_ID FROM FS_RT_PLAN RTP  WHERE RTP.QUOTE_ID = (SELECT QUOTE_ID FROM QMS_QUOTE_MASTER QMS WHERE QMS.ID=p_Quoteid) )
 LOOP
  SELECT Rtplan_Seq.NEXTVAL INTO v_Rt_Plan_Id FROM Dual;
      INSERT INTO FS_RT_PLAN
      (Rt_Plan_Id,
       Quote_Id,
       Orig_Trml_Id,
       Dest_Trml_Id,
       Orig_Loc_Id,
       Dest_Loc_Id,
       Shipper_Id,
       Consignee_Id,
       Prmy_Mode,
       Over_Write_Flag,
       Shpmnt_Status,
       Crtd_Timestmp)
      SELECT v_Rt_Plan_Id,
             p_Newquoteid,
             Orig_Trml_Id,
             Dest_Trml_Id,
             Orig_Loc_Id,
             Dest_Loc_Id,
             Shipper_Id,
             Consignee_Id,
             Prmy_Mode,
             Over_Write_Flag,
             Shpmnt_Status,
             Crtd_Timestmp
        FROM FS_RT_PLAN
       WHERE RT_PLAN_ID IN
             (  SELECT RP.RT_PLAN_ID
                                  FROM FS_RT_PLAN RP, QMS_QUOTE_MASTER QM
                                 WHERE RP.Quote_Id =  QM.Quote_Id
                                      AND RP.ORIG_LOC_ID = QM.ORIGIN_LOCATION
                                       AND RP.DEST_LOC_ID = QM.DEST_LOCATION
                                       AND  QM.ID= p_Quoteid)
            AND  RT_PLAN_ID = I.RT_PLAN_ID;


    INSERT INTO FS_RT_LEG
      (Rt_Plan_Id,
       Serial_No,
       Leg_Type,
       Orig_Loc,
       Dest_Loc,
       Shpmnt_Mode,
       Shpmnt_Status,
       Auto_Mnul_Flag,
       Mster_Doc_Id,
       Leg_Valid_Flag,
       Pieces_Received,
       Received_Date,
       Remarks,
       Costamount,
       Orig_Trml_Id,
       Dest_Trml_Id)
      SELECT v_Rt_Plan_Id,
             Serial_No,
             Leg_Type,
             Orig_Loc,
             Dest_Loc,
             Shpmnt_Mode,
             Shpmnt_Status,
             Auto_Mnul_Flag,
             Mster_Doc_Id,
             Leg_Valid_Flag,
             Pieces_Received,
             Received_Date,
             Remarks,
             Costamount,
             Orig_Trml_Id,
             Dest_Trml_Id
        FROM FS_RT_LEG
       WHERE Rt_Plan_Id IN
             (  SELECT RP.RT_PLAN_ID
                                  FROM FS_RT_PLAN RP, QMS_QUOTE_MASTER QM
                                 WHERE RP.Quote_Id =  QM.Quote_Id
                                      AND RP.ORIG_LOC_ID = QM.ORIGIN_LOCATION
                                       AND RP.DEST_LOC_ID = QM.DEST_LOCATION
                                       AND  QM.ID= p_Quoteid)
            AND  RT_PLAN_ID = I.RT_PLAN_ID;
 END  LOOP;

    IF UPPER(p_Sellbuyflag) = 'BR' THEN
      v_Sql := ' and (qr.buyrate_id,decode(qr.sell_buy_flag,''RSR'',decode(qr.margin_discount_flag,''M'',''BR''),''CSR'',decode(qr.margin_discount_flag,''M'',''BR''),qr.sell_buy_flag),qr.rate_lane_no) not in (select old_buycharge_id,sell_buy_flag,old_lane_no from qms_quotes_updated where quote_id=' ||
               p_Quoteid || ' and changedesc=' || '''' || p_Changedesc || '''' || ')';
    ELSIF (UPPER(p_Sellbuyflag) = 'RSR' OR UPPER(p_Sellbuyflag) = 'CSR') THEN
      v_Sql := ' and (qr.sell_buy_flag,qr.sellrate_id,qr.buyrate_id,qr.rate_lane_no) not in (select sell_buy_flag,old_sellcharge_id,old_buycharge_id,old_lane_no from qms_quotes_updated where quote_id=' ||
               p_Quoteid || ' and changedesc=' || '''' || p_Changedesc || '''' || ')';
    ELSIF UPPER(p_Sellbuyflag) = 'B' THEN
      /*      v_Sql := ' and (decode(qr.SELL_BUY_FLAG,''S'',decode(qr.margin_discount_flag,''M'',''B''),qr.SELL_BUY_FLAG), qr.BUYRATE_ID) not in (select sell_buy_flag,old_buycharge_id from qms_quotes_updated where quote_id=' ||
      p_Quoteid || ' and changedesc=' || '''' || p_Changedesc || '''' || ')';*/

      v_Sql := ' and (decode(qr.SELL_BUY_FLAG,''S'',decode(qr.margin_discount_flag,''M'',''B''),qr.SELL_BUY_FLAG), qr.BUYRATE_ID,qr.charge_at) not in (select sell_buy_flag,old_buycharge_id,charge_at from qms_quotes_updated where quote_id=' ||
               p_Quoteid || ' and changedesc=' || '''' || p_Changedesc || '''' || ')';
    ELSIF UPPER(p_Sellbuyflag) = 'S' THEN
      /* v_Sql := ' and (qr.SELL_BUY_FLAG, qr.BUYRATE_ID, qr.SELLRATE_ID) not in (select sell_buy_flag, old_buycharge_id, old_sellcharge_id from qms_quotes_updated where quote_id=' ||
      p_Quoteid || ' and changedesc=' || '''' || p_Changedesc || '''' || ')';*/

      v_Sql := ' and (qr.SELL_BUY_FLAG, qr.BUYRATE_ID, qr.SELLRATE_ID,qr.charge_at) not in (select sell_buy_flag, old_buycharge_id, old_sellcharge_id,charge_at from qms_quotes_updated where quote_id=' ||
               p_Quoteid || ' and changedesc=' || '''' || p_Changedesc || '''' || ')';
    ELSIF UPPER(p_Sellbuyflag) = 'BC' THEN
      v_Sql := ' and qm.id=qr.quote_id and (qr.buyrate_id,decode(qr.charge_at,''Pickup'',qm.shipperzones,''Delivery'',qm.consigneezones),decode(qr.sell_buy_flag,''SC'',decode(qr.margin_discount_flag,''M'',''BC''),qr.sell_buy_flag)) not in (select old_buycharge_id,zone_code,sell_buy_flag from qms_quotes_updated where quote_id=' ||
               p_Quoteid || ' and changedesc=' || '''' || p_Changedesc || '''' || ')';
    ELSIF UPPER(p_Sellbuyflag) = 'SC' THEN
      v_Sql := ' and qm.id=qr.quote_id and (qr.buyrate_id,qr.SELLRATE_ID,decode(qr.charge_at,''Pickup'',qm.shipperzones,''Delivery'',qm.consigneezones),qr.sell_buy_flag) not in (select old_buycharge_id,old_sellcharge_id,zone_code,sell_buy_flag from qms_quotes_updated where quote_id=' ||
               p_Quoteid || ' and changedesc=' || '''' || p_Changedesc || '''' || ')';
    END IF;
    /*  If (Upper(p_Sellbuyflag) = 'BC' Or Upper(p_Sellbuyflag) = 'SC') Then
      Execute Immediate ('INSERT INTO qms_quote_rates SELECT quote_master_seq.currval,qr.SELL_BUY_FLAG, qr.BUYRATE_ID, qr.SELLRATE_ID, qr.RATE_LANE_NO, qr.CHARGE_ID, qr.CHARGE_DESCRIPTION, qr.MARGIN_DISCOUNT_FLAG, qr.MARGIN_TYPE, qr.MARGIN, qr.DISCOUNT_TYPE, qr.DISCOUNT, qr.NOTES, qr.QUOTE_REFNO, qr.BREAK_POINT, qr.CHARGE_AT, qr.BUY_RATE, qr.R_SELL_RATE, qr.RT_PLAN_ID, qr.SERIAL_NO, SEQ_QMS_QUOTE_RATES.NEXTVAL, qr.ZONE_CODE, qr.line_no,qr.MARGIN_TEST_FLAG ' ||
                        ' FROM qms_quote_rates qr, qms_quote_master qm WHERE qr.quote_id=' ||
                        p_Quoteid || ' and qr.Charge_Description  in(select chargedescid from qms_charge_groupsmaster where chargegroup_id in (
                   Select chargegroupid from qms_quote_chargegroupdtl where quote_id= '|| p_Quoteid|| ') and inactivate=''N'')'|| v_Sql);
    Else
      Execute Immediate ('INSERT INTO qms_quote_rates SELECT quote_master_seq.currval,qr.SELL_BUY_FLAG, qr.BUYRATE_ID, qr.SELLRATE_ID, qr.RATE_LANE_NO, qr.CHARGE_ID, qr.CHARGE_DESCRIPTION, qr.MARGIN_DISCOUNT_FLAG, qr.MARGIN_TYPE, qr.MARGIN, qr.DISCOUNT_TYPE, qr.DISCOUNT, qr.NOTES, qr.QUOTE_REFNO, qr.BREAK_POINT, qr.CHARGE_AT, qr.BUY_RATE, qr.R_SELL_RATE, qr.RT_PLAN_ID, qr.SERIAL_NO, SEQ_QMS_QUOTE_RATES.NEXTVAL, qr.ZONE_CODE, qr.line_no,qr.MARGIN_TEST_FLAG ' ||
                        ' FROM qms_quote_rates qr WHERE qr.quote_id=' ||
                        p_Quoteid ||' and qr.Charge_Description  in(select chargedescid from qms_charge_groupsmaster where chargegroup_id in (
                   Select chargegroupid from qms_quote_chargegroupdtl where quote_id= '|| p_Quoteid|| ') and inactivate=''N'')'|| v_Sql);
    End If;*/

    --@@Commented and Modified by Kameswari for the WPBN issue-129343
    IF (UPPER(p_Sellbuyflag) = 'BC' OR UPPER(p_Sellbuyflag) = 'SC') THEN

     /* Execute Immediate (' INSERT INTO qms_quote_rates(quote_id,id,sell_buy_flag,buyrate_id,sellrate_id,rate_lane_no,charge_id,charge_description,margin_discount_flag,margin_type,margin,discount_type,discount,notes,quote_refno,break_point,charge_at,buy_rate,r_sell_rate,rt_plan_id ,serial_no ,zone_code,line_no,margin_test_flag) select quote_master_seq.currval,SEQ_QMS_QUOTE_RATES.NEXTVAL,t.* from ( SELECT qr.SELL_BUY_FLAG, qr.BUYRATE_ID, qr.SELLRATE_ID, qr.RATE_LANE_NO, qr.CHARGE_ID, qr.CHARGE_DESCRIPTION, qr.MARGIN_DISCOUNT_FLAG, qr.MARGIN_TYPE, qr.MARGIN, qr.DISCOUNT_TYPE, qr.DISCOUNT, qr.NOTES, qr.QUOTE_REFNO, qr.BREAK_POINT, qr.CHARGE_AT, qr.BUY_RATE, qr.R_SELL_RATE, qr.RT_PLAN_ID, qr.SERIAL_NO, qr.ZONE_CODE, qr.line_no,qr.MARGIN_TEST_FLAG ' ||
                        ' FROM qms_quote_rates qr, qms_quote_master qm   WHERE qr.quote_id=' ||
                        p_Quoteid ||
                        ' and (qr.sell_buy_flag =''B'' or qr.sell_buy_flag=''S'') and qr.Charge_Description  in(select chargedescid from qms_charge_groupsmaster where chargegroup_id in (
                   Select chargegroupid from qms_quote_chargegroupdtl where quote_id= ' ||
                        p_Quoteid || ') and inactivate=''N'')' || v_Sql ||
                        ' Union  SELECT qr.SELL_BUY_FLAG, qr.BUYRATE_ID, qr.SELLRATE_ID, qr.RATE_LANE_NO, qr.CHARGE_ID, qr.CHARGE_DESCRIPTION, qr.MARGIN_DISCOUNT_FLAG, qr.MARGIN_TYPE, qr.MARGIN, qr.DISCOUNT_TYPE, qr.DISCOUNT, qr.NOTES, qr.QUOTE_REFNO, qr.BREAK_POINT, qr.CHARGE_AT, qr.BUY_RATE, qr.R_SELL_RATE, qr.RT_PLAN_ID, qr.SERIAL_NO,  qr.ZONE_CODE, qr.line_no,qr.MARGIN_TEST_FLAG ' ||
                        ' FROM qms_quote_rates qr, qms_quote_master qm  WHERE qr.quote_id=' ||
                        p_Quoteid ||
                        'and (qr.sell_buy_flag =''BR'' or qr.sell_buy_flag=''RSR''  or qr.sell_buy_flag=''SBR''  or qr.sell_buy_flag=''CSR'' or qr.sell_buy_flag=''BC'' or qr.sell_buy_flag=''SC'' )' ||
                        v_Sql || ')t '); */--@@Modified by Kameswari for the WPBN issue-13237
       EXECUTE IMMEDIATE (' INSERT INTO qms_quote_rates(quote_id,id,sell_buy_flag,buyrate_id,sellrate_id,rate_lane_no,version_no,charge_id,charge_description,margin_discount_flag,margin_type,margin,discount_type,discount,notes,quote_refno,break_point,charge_at,buy_rate,r_sell_rate,rt_plan_id ,serial_no ,zone_code,line_no,margin_test_flag,frequency,carrier,transit_time,rate_validity,frequency_checked,carrier_checked,transit_checked,validity_checked) select quote_master_seq.currval,SEQ_QMS_QUOTE_RATES.NEXTVAL,t.* from ( SELECT qr.SELL_BUY_FLAG, qr.BUYRATE_ID, qr.SELLRATE_ID, qr.RATE_LANE_NO, qr.version_no, qr.CHARGE_ID, qr.CHARGE_DESCRIPTION, qr.MARGIN_DISCOUNT_FLAG, qr.MARGIN_TYPE, qr.MARGIN, qr.DISCOUNT_TYPE, qr.DISCOUNT, qr.NOTES, qr.QUOTE_REFNO, qr.BREAK_POINT, qr.CHARGE_AT, qr.BUY_RATE, qr.R_SELL_RATE, qr.RT_PLAN_ID, qr.SERIAL_NO, qr.ZONE_CODE, qr.line_no,qr.MARGIN_TEST_FLAG ,qr.frequency,qr.carrier,qr.transit_time,qr.rate_validity,qr.frequency_checked,qr.carrier_checked,qr.transit_checked,qr.validity_checked' ||
                        ' FROM qms_quote_rates qr, qms_quote_master qm   WHERE qr.quote_id=' ||
                        p_Quoteid ||
                        ' and (qr.sell_buy_flag =''B'' or qr.sell_buy_flag=''S'') and qr.Charge_Description  in(select chargedescid from qms_charge_groupsmaster where chargegroup_id in (
                   SELECT chargegroupid FROM QMS_QUOTE_CHARGEGROUPDTL WHERE quote_id= ' ||
                        p_Quoteid || ') and inactivate=''N'')' || v_Sql ||
                        ' Union  SELECT qr.SELL_BUY_FLAG, qr.BUYRATE_ID, qr.SELLRATE_ID, qr.RATE_LANE_NO, qr.VERSION_NO, qr.CHARGE_ID, qr.CHARGE_DESCRIPTION, qr.MARGIN_DISCOUNT_FLAG, qr.MARGIN_TYPE, qr.MARGIN, qr.DISCOUNT_TYPE, qr.DISCOUNT, qr.NOTES, qr.QUOTE_REFNO, qr.BREAK_POINT, qr.CHARGE_AT, qr.BUY_RATE, qr.R_SELL_RATE, qr.RT_PLAN_ID, qr.SERIAL_NO,  qr.ZONE_CODE, qr.line_no,qr.MARGIN_TEST_FLAG,qr.frequency,qr.carrier,qr.transit_time,qr.rate_validity,qr.frequency_checked,qr.carrier_checked,qr.transit_checked,qr.validity_checked ' ||
                        ' FROM qms_quote_rates qr, qms_quote_master qm  WHERE qr.quote_id=' ||
                        p_Quoteid ||
                        'and (qr.sell_buy_flag =''BR'' or qr.sell_buy_flag=''RSR''  or qr.sell_buy_flag=''SBR''  or qr.sell_buy_flag=''CSR'' or qr.sell_buy_flag=''BC'' or qr.sell_buy_flag=''SC'' )' ||
                        v_Sql || ')t '); --@@Modified for the WPBN issues-146448,146968 on 19/12/08
      /*   'and (qr.sell_buy_flag =''BR'' or qr.sell_buy_flag=''RSR'' or qr.sell_buy_flag=''BC'' or qr.sell_buy_flag=''SC'' )' ||
                              v_Sql || ')t ');
      */
    ELSIF (UPPER(p_Sellbuyflag) = 'BR' OR UPPER(p_Sellbuyflag) = 'RSR') THEN
      EXECUTE IMMEDIATE (' INSERT INTO qms_quote_rates(quote_id,id,sell_buy_flag,buyrate_id,sellrate_id,rate_lane_no,charge_id,charge_description,margin_discount_flag,margin_type,margin,discount_type,discount,notes,quote_refno,break_point,charge_at,buy_rate,r_sell_rate,rt_plan_id ,serial_no ,zone_code,line_no,margin_test_flag) select quote_master_seq.currval,SEQ_QMS_QUOTE_RATES.NEXTVAL,t.* from (SELECT qr.SELL_BUY_FLAG, qr.BUYRATE_ID, qr.SELLRATE_ID, qr.RATE_LANE_NO, qr.CHARGE_ID, qr.CHARGE_DESCRIPTION, qr.MARGIN_DISCOUNT_FLAG, qr.MARGIN_TYPE, qr.MARGIN, qr.DISCOUNT_TYPE, qr.DISCOUNT, qr.NOTES, qr.QUOTE_REFNO, qr.BREAK_POINT, qr.CHARGE_AT, qr.BUY_RATE, qr.R_SELL_RATE, qr.RT_PLAN_ID, qr.SERIAL_NO,  qr.ZONE_CODE, qr.line_no,qr.MARGIN_TEST_FLAG ' ||
                        ' FROM qms_quote_rates qr WHERE qr.quote_id=' ||
                        p_Quoteid ||
                        '  and (qr.sell_buy_flag =''B'' or qr.sell_buy_flag=''S'') and qr.Charge_Description  in(select chargedescid from qms_charge_groupsmaster where chargegroup_id in (
                   SELECT chargegroupid FROM QMS_QUOTE_CHARGEGROUPDTL WHERE quote_id= ' ||
                        p_Quoteid || ') and inactivate=''N'')' || v_Sql ||
                        'Union SELECT qr.SELL_BUY_FLAG, qr.BUYRATE_ID, qr.SELLRATE_ID, qr.RATE_LANE_NO, qr.CHARGE_ID, qr.CHARGE_DESCRIPTION, qr.MARGIN_DISCOUNT_FLAG, qr.MARGIN_TYPE, qr.MARGIN, qr.DISCOUNT_TYPE, qr.DISCOUNT, qr.NOTES, qr.QUOTE_REFNO, qr.BREAK_POINT, qr.CHARGE_AT, qr.BUY_RATE, qr.R_SELL_RATE, qr.RT_PLAN_ID, qr.SERIAL_NO,  qr.ZONE_CODE, qr.line_no,qr.MARGIN_TEST_FLAG ' ||
                        ' FROM qms_quote_rates qr WHERE qr.quote_id=' ||
                        p_Quoteid ||
                        ' and (qr.sell_buy_flag =''BC'' or qr.sell_buy_flag =''SBR'' or qr.sell_buy_flag =''BR'' or qr.sell_buy_flag =''CSR'' or qr.sell_buy_flag =''RSR'' or qr.sell_buy_flag=''SC'')' ||
                        v_Sql || ' )t ');

    ELSE
      /*Execute Immediate (' INSERT INTO qms_quote_rates(quote_id,id,sell_buy_flag,buyrate_id,sellrate_id,rate_lane_no,charge_id,charge_description,margin_discount_flag,margin_type,margin,discount_type,discount,notes,quote_refno,break_point,charge_at,buy_rate,r_sell_rate,rt_plan_id ,serial_no ,zone_code,line_no,margin_test_flag) select quote_master_seq.currval,SEQ_QMS_QUOTE_RATES.NEXTVAL,t.* from (SELECT qr.SELL_BUY_FLAG, qr.BUYRATE_ID, qr.SELLRATE_ID, qr.RATE_LANE_NO, qr.CHARGE_ID, qr.CHARGE_DESCRIPTION, qr.MARGIN_DISCOUNT_FLAG, qr.MARGIN_TYPE, qr.MARGIN, qr.DISCOUNT_TYPE, qr.DISCOUNT, qr.NOTES, qr.QUOTE_REFNO, qr.BREAK_POINT, qr.CHARGE_AT, qr.BUY_RATE, qr.R_SELL_RATE, qr.RT_PLAN_ID, qr.SERIAL_NO,  qr.ZONE_CODE, qr.line_no,qr.MARGIN_TEST_FLAG ' ||
                        ' FROM qms_quote_rates qr WHERE  qr.quote_id=' ||
                        p_Quoteid ||
                        '  and qr.sell_buy_flag  in (''B'' , ''S'') and qr.Charge_Description  in(select chargedescid from qms_charge_groupsmaster where chargegroup_id in (
                   Select chargegroupid from qms_quote_chargegroupdtl where quote_id= ' ||
                        p_Quoteid || ') and inactivate=''N'')' || v_Sql ||
                        'Union SELECT qr.SELL_BUY_FLAG, qr.BUYRATE_ID, qr.SELLRATE_ID, qr.RATE_LANE_NO, qr.CHARGE_ID, qr.CHARGE_DESCRIPTION, qr.MARGIN_DISCOUNT_FLAG, qr.MARGIN_TYPE, qr.MARGIN, qr.DISCOUNT_TYPE, qr.DISCOUNT, qr.NOTES, qr.QUOTE_REFNO, qr.BREAK_POINT, qr.CHARGE_AT, qr.BUY_RATE, qr.R_SELL_RATE, qr.RT_PLAN_ID, qr.SERIAL_NO,  qr.ZONE_CODE, qr.line_no,qr.MARGIN_TEST_FLAG,qr.frequency,qr.carrier,qr.transit_time,qr.rate_validity,qr.frequency_checked,qr.carrier_checked,qr.transit_checked,qr.validity_checked ' ||
                        ' FROM qms_quote_rates qr WHERE qr.quote_id=' ||
                        p_Quoteid ||
                        ' and qr.sell_buy_flag in (''BC'',''SC'',''BR'',''RSR'',''CSR'',''SBR'')' ||
                        v_Sql || ' )t '); */--@@Modified by Kameswari for the WPBN issue-13237
     EXECUTE IMMEDIATE (' INSERT INTO qms_quote_rates(quote_id,id,sell_buy_flag,buyrate_id,sellrate_id,rate_lane_no,version_no,charge_id,charge_description,margin_discount_flag,margin_type,margin,discount_type,discount,notes,quote_refno,break_point,charge_at,buy_rate,r_sell_rate,rt_plan_id ,serial_no ,zone_code,line_no,margin_test_flag,frequency,carrier,transit_time,rate_validity,frequency_checked,carrier_checked,transit_checked,validity_checked) select quote_master_seq.currval,SEQ_QMS_QUOTE_RATES.NEXTVAL,t.* from (SELECT qr.SELL_BUY_FLAG, qr.BUYRATE_ID, qr.SELLRATE_ID, qr.RATE_LANE_NO, qr.VERSION_NO,qr.CHARGE_ID, qr.CHARGE_DESCRIPTION, qr.MARGIN_DISCOUNT_FLAG, qr.MARGIN_TYPE, qr.MARGIN, qr.DISCOUNT_TYPE, qr.DISCOUNT, qr.NOTES, qr.QUOTE_REFNO, qr.BREAK_POINT, qr.CHARGE_AT, qr.BUY_RATE, qr.R_SELL_RATE, qr.RT_PLAN_ID, qr.SERIAL_NO,  qr.ZONE_CODE, qr.line_no,qr.MARGIN_TEST_FLAG,qr.frequency,qr.carrier,qr.transit_time,qr.rate_validity,qr.frequency_checked,qr.carrier_checked,qr.transit_checked,qr.validity_checked ' ||
                         ' FROM qms_quote_rates qr WHERE  qr.quote_id=' ||
                        p_Quoteid ||
                        '  and qr.sell_buy_flag  in (''B'' , ''S'') and qr.Charge_Description  in(select chargedescid from qms_charge_groupsmaster where chargegroup_id in (
                   SELECT chargegroupid FROM QMS_QUOTE_CHARGEGROUPDTL WHERE quote_id= ' ||
                        p_Quoteid || ') and inactivate=''N'')' || v_Sql ||
                        'Union SELECT qr.SELL_BUY_FLAG, qr.BUYRATE_ID, qr.SELLRATE_ID, qr.RATE_LANE_NO, qr.VERSION_NO,qr.CHARGE_ID, qr.CHARGE_DESCRIPTION, qr.MARGIN_DISCOUNT_FLAG, qr.MARGIN_TYPE, qr.MARGIN, qr.DISCOUNT_TYPE, qr.DISCOUNT, qr.NOTES, qr.QUOTE_REFNO, qr.BREAK_POINT, qr.CHARGE_AT, qr.BUY_RATE, qr.R_SELL_RATE, qr.RT_PLAN_ID, qr.SERIAL_NO,  qr.ZONE_CODE, qr.line_no,qr.MARGIN_TEST_FLAG ,qr.frequency,qr.carrier,qr.transit_time,qr.rate_validity,qr.frequency_checked,qr.carrier_checked,qr.transit_checked,qr.validity_checked ' ||
                        ' FROM qms_quote_rates qr WHERE qr.quote_id=' ||
                        p_Quoteid ||
                        ' and qr.sell_buy_flag in (''BC'',''SC'',''BR'',''RSR'',''CSR'',''SBR'')' ||
                        v_Sql || ' )t ');--@@Modified for the WPBN issues-146448,146968 on 19/12/08
      /* ' and qr.sell_buy_flag in (''BC'',''SC'',''BR'',''RSR'')' ||
                              v_Sql || ' )t ');
      */
    END IF;
    COUNT :=0;

    --@@WPBN issue-129343
    IF UPPER(p_Sellbuyflag) = 'BR' OR (UPPER(p_Sellbuyflag) = 'RSR' OR UPPER(p_Sellbuyflag) = 'CSR')THEN
    v_count :=0;
      FOR i IN (SELECT BUYRATE_ID,RATE_LANE_NO,break_point,Frequency_Checked,Carrier_Checked,Transit_Checked,Validity_Checked,Margin_Discount_Flag,Margin,Margin_type,Discount,Discount_type,Serial_No,Charge_Description,Charge_at,line_no FROM QMS_QUOTE_RATES WHERE quote_id=p_Quoteid AND sell_buy_flag IN ('BR','RSR','CSR'))
      LOOP
      --@@Added by subrahmanyam for 188024 on 05-Nov-09
      v_Frequency_Checked := i.frequency_checked;
      v_Carrier_Checked := i.carrier_checked;
      v_Transit_Checked := i.transit_checked;
      v_Validity_Checked := i.validity_checked;
      V_charge_description  := i.charge_description;
      v_charge_at           := i.charge_at;
      v_serial_no           := i.serial_no;
          --@@Ended by subrahmanyam for 188024 on 05-Nov-09
      INSERT INTO QMS_QUOTE_RATES
        (Quote_Id,
         Sell_Buy_Flag,
         Sellrate_Id,
         Buyrate_Id,
         Rate_Lane_No,
         Version_No,     --@@Added for the WPBN issues-146448,146968 on 19/12/08
         Charge_Description,
         Margin_Discount_Flag,
         Margin_Type,
         Margin,
         Discount_type,
         Discount,
         Break_Point,
         Line_No,
         Buy_Rate,
         R_SELL_RATE,
         Charge_At,
         Serial_No,
         Id,
         Frequency,
         Carrier,
         Transit_Time,
         Rate_Validity,
         Frequency_checked,
         Carrier_Checked,
         Transit_checked,
         Validity_checked
      )
        SELECT p_Newuniqueid,
               Qu.Sell_Buy_Flag,
               Qu.New_Sellcharge_Id,
               Qu.New_Buycharge_Id,
               Qu.New_Lane_No,
               Qu.New_Version_No, --@@Added for the WPBN issues-146448,146968 on 19/12/08
               /*(Select Distinct Iqr.Charge_Description
                  From Qms_Quote_Rates Iqr
                 Where Iqr.Quote_Id = Qu.Quote_Id
                   And Decode(Iqr.Sell_Buy_Flag,
                              'RSR',
                              Decode(Iqr.Margin_Discount_Flag, 'M', 'BR'),
                              'CSR',
                              Decode(Iqr.Margin_Discount_Flag, 'M', 'BR'),
                              Iqr.Sell_Buy_Flag) = Qu.Sell_Buy_Flag
                    And Iqr.Buyrate_Id = Qu.Old_Buycharge_Id
                   And Iqr.Rate_Lane_No = Qu.Old_Lane_No) Charge_Desc,*/
                  i.charge_description,
              /* (Select Distinct Iqr.Margin_Discount_Flag
                  From Qms_Quote_Rates Iqr
                 Where Iqr.Quote_Id = Qu.Quote_Id
                   And Decode(Iqr.Sell_Buy_Flag,
                              'RSR',
                              Decode(Iqr.Margin_Discount_Flag, 'M', 'BR'),
                              'CSR',
                              Decode(Iqr.Margin_Discount_Flag, 'M', 'BR'),
                              Iqr.Sell_Buy_Flag) = Qu.Sell_Buy_Flag
                   And  Iqr.Sell_Buy_Flag = Qu.Sell_Buy_Flag           --@@Modified by Kameswari for the WPBN issue-140462
                   And Iqr.Buyrate_Id = Qu.Old_Buycharge_Id
                   And Iqr.Rate_Lane_No = Qu.Old_Lane_No) Margin_Discount_Flag,*/
               i.margin_discount_flag,
               i.margin_type,
               i.margin,
               i.discount_type,
               i.discount,
               Bd.Weight_Break_Slab,
               i.Line_No,
               Bd.Chargerate,
               (SELECT chargerate FROM QMS_REC_CON_SELLRATESDTL WHERE buyrateid=Bd.Buyrateid AND lane_no=Bd.Lane_No AND weightbreakslab=Bd.Weight_Break_Slab AND version_no=Bd.Version_No AND ai_flag='A'),
               /*(Select Distinct Iqr.Charge_At
                  From Qms_Quote_Rates Iqr
                 Where Iqr.Quote_Id = Qu.Quote_Id
                   And Decode(Iqr.Sell_Buy_Flag,
                              'RSR',
                              Decode(Iqr.Margin_Discount_Flag, 'M', 'BR'),
                              'CSR',
                              Decode(Iqr.Margin_Discount_Flag, 'M', 'BR'),
                              Iqr.Sell_Buy_Flag) = Qu.Sell_Buy_Flag
                   And Iqr.Buyrate_Id = Qu.Old_Buycharge_Id
                   And Iqr.Rate_Lane_No = Qu.Old_Lane_No) Charge_At,*/
                   i.charge_at,
               /*(Select Distinct Iqr.Serial_No
                  From Qms_Quote_Rates Iqr
                 Where Iqr.Quote_Id = Qu.Quote_Id
                   And Decode(Iqr.Sell_Buy_Flag,
                              'RSR',
                              Decode(Iqr.Margin_Discount_Flag, 'M', 'BR'),
                              'CSR',
                              Decode(Iqr.Margin_Discount_Flag, 'M', 'BR'),
                              Iqr.Sell_Buy_Flag) = Qu.Sell_Buy_Flag
                   And Iqr.Buyrate_Id = Qu.Old_Buycharge_Id
                   And Iqr.Rate_Lane_No = Qu.Old_Lane_No) Serial_No,*/
                   i.serial_no,
               Seq_Qms_Quote_Rates.NEXTVAL,
               Bd.Frequency,
               Bd.Carrier_Id,
               Bd.Transit_Time,
               Bd.Valid_Upto,
               i.frequency_checked,
               i.carrier_checked,
               i.transit_checked,
               i.validity_checked
          FROM QMS_QUOTES_UPDATED Qu, QMS_BUYRATES_DTL Bd
         WHERE Qu.New_Buycharge_Id = Bd.Buyrateid
                                                 --@@Added for the WPBN issue-146968 on 02/01/09
        /* And Bd.Weight_Break_Slab=Qr.Break_Point*/
           AND Qu.New_Lane_No = Bd.Lane_No
           AND Qu.New_version_no=Bd.version_no
           AND Qu.New_version_no=(SELECT new_version_no FROM QMS_QUOTES_UPDATED WHERE quote_id=p_Quoteid AND changedesc=p_Changedesc AND CONFIRM_FLAG IS NULL)
           AND Bd.Weight_Break_Slab=i.break_point
           AND Qu.Quote_Id = p_Quoteid
           AND Qu.Changedesc = p_Changedesc
           AND  Qu.Old_Buycharge_Id=i.buyrate_id
           AND   Qu.Old_Lane_No=i.rate_lane_no;
          v_count := v_count+1;      --@@Added by subrahmanyam for 188024 on 05-Nov-09
           END LOOP;
      --@@Added by subrahmanyam for 188024 on 05-Nov-09
           IF UPPER(p_Sellbuyflag) = 'BR' THEN
                 V_Margin_Discount_Flag := 'M';
                 V_Margin_Type          := 'P';
                 V_Margin               := '0.00000';
                 V_Discount_type        := '';
                 V_Discount             := '0.00000';
           ELSIF UPPER(p_Sellbuyflag) = 'RSR' THEN
                 V_Margin_Discount_Flag := 'D';
                 V_Margin_Type          := '';
                 V_Margin               := '0.00000';
                 V_Discount_type        := 'P';
                 V_Discount             := '0.00000';

           END IF;
           IF (v_Shmode = '2' AND v_Weightbreak = 'LIST') THEN

           FOR j IN ( SELECT QR.BREAK_POINT,QR.BUYRATE_ID,QR.RATE_LANE_NO,QR.VERSION_NO FROM QMS_QUOTE_RATES QR ,QMS_QUOTES_UPDATED QU WHERE  QR.QUOTE_ID=P_NEWUNIQUEID AND QR.BUYRATE_ID= QU.NEW_BUYCHARGE_ID
           	   AND QR.RATE_LANE_NO=QU.NEW_LANE_NO AND QR.VERSION_NO=NEW_VERSION_NO AND QU.QUOTE_ID=P_QUOTEID AND QU.CHANGEDESC=P_CHANGEDESC)
             LOOP
           BEGIN
            FOR K IN (SELECT DISTINCT BD.WEIGHT_BREAK_SLAB,BD.CHARGERATE,BD.FREQUENCY,BD.TRANSIT_TIME,BD.CARRIER_ID,BD.VALID_UPTO  FROM QMS_BUYRATES_DTL BD WHERE
              BD.BUYRATEID=J.BUYRATE_ID
              AND  BD.LANE_NO=J.RATE_LANE_NO
              AND BD.VERSION_NO = J.VERSION_NO
              AND NOT EXISTS(SELECT 'X' FROM
              QMS_QUOTE_RATES QR WHERE  QR.QUOTE_ID=P_NEWUNIQUEID AND QR.BUYRATE_ID=BD.BUYRATEID
              AND QR.RATE_LANE_NO=BD.LANE_NO AND QR.VERSION_NO=BD.VERSION_NO AND QR.BREAK_POINT=BD.WEIGHT_BREAK_SLAB )
              AND INSTR(BD.WEIGHT_BREAK_SLAB , j.BREAK_POINT,1)>0)
            LOOP

             INSERT INTO QMS_QUOTE_RATES
        (Quote_Id,
         Sell_Buy_Flag,
         Sellrate_Id,
         Buyrate_Id,
         Rate_Lane_No,
         Version_No,
         Charge_Description,
         Margin_Discount_Flag,
         Margin_Type,
         Margin,
         Discount_type,
         Discount,
         Break_Point,
         Line_No,
         Buy_Rate,
         R_SELL_RATE,
         Charge_At,
         Serial_No,
         Id,
         Frequency,
         Carrier,
         Transit_Time,
         Rate_Validity,
         Frequency_checked,
         Carrier_Checked,
         Transit_checked,
         Validity_checked
      )
        SELECT p_Newuniqueid,
               Qu.Sell_Buy_Flag,
               Qu.New_Sellcharge_Id,
               Qu.New_Buycharge_Id,
               Qu.New_Lane_No,
               Qu.New_Version_No,
               V_charge_description,
               V_Margin_Discount_Flag,
               V_Margin_Type,
               V_Margin,
               V_Discount_type,
               V_Discount,
               K.WEIGHT_BREAK_SLAB,
              v_count,
               K.Chargerate,
               (SELECT chargerate FROM QMS_REC_CON_SELLRATESDTL WHERE buyrateid=Bd.Buyrateid AND lane_no=Bd.Lane_No AND weightbreakslab=Bd.Weight_Break_Slab AND version_no=Bd.Version_No AND ai_flag='A'),
               v_charge_at,
               v_serial_no,
               Seq_Qms_Quote_Rates.NEXTVAL,
               K.FREQUENCY,
               K.Carrier_Id,
               K.Transit_Time,
               K.Valid_Upto,
               V_frequency_checked,
               V_carrier_checked,
               V_transit_checked,
               v_Validity_Checked
          FROM QMS_QUOTES_UPDATED Qu, QMS_BUYRATES_DTL Bd
         WHERE Qu.New_Buycharge_Id = Bd.Buyrateid

           AND Qu.New_Lane_No = Bd.Lane_No
           AND Qu.New_version_no=Bd.version_no
           AND Qu.New_version_no=(SELECT new_version_no FROM QMS_QUOTES_UPDATED WHERE quote_id=p_Quoteid AND changedesc=p_Changedesc and confirm_flag is null)
           AND Bd.Weight_Break_Slab=K.Weight_Break_Slab
           AND Qu.Quote_Id = p_Quoteid
           AND Qu.Changedesc = p_Changedesc
           AND  Qu.Old_Buycharge_Id=bd.buyrateid
           AND   Qu.Old_Lane_No=bd.lane_no;
         v_count := v_count+1;
            END LOOP;
            EXCEPTION WHEN NO_DATA_FOUND
           THEN
            DBMS_OUTPUT.put_line('NO EXTRA CHARGES FOUND..');
          END;
             END LOOP;
           ELSE
           BEGIN
           	  FOR J IN ( SELECT DISTINCT BD.WEIGHT_BREAK_SLAB,BD.CHARGERATE,BD.BUYRATEID,BD.LANE_NO,BD.VERSION_NO,BD.FREQUENCY,BD.CARRIER_ID,BD.TRANSIT_TIME,BD.VALID_UPTO FROM QMS_QUOTES_UPDATED QU,QMS_BUYRATES_DTL BD WHERE QUOTE_ID=P_QUOTEID AND CHANGEDESC=P_CHANGEDESC
                        AND QU.NEW_BUYCHARGE_ID = BD.BUYRATEID
                        AND QU.NEW_LANE_NO = BD.LANE_NO
                        AND QU.NEW_VERSION_NO=BD.VERSION_NO AND NOT EXISTS(SELECT 'X' FROM
                        QMS_QUOTE_RATES QR WHERE  QR.QUOTE_ID=P_NEWUNIQUEID AND QR.BUYRATE_ID=BD.BUYRATEID
                        AND QR.RATE_LANE_NO=BD.LANE_NO AND QR.VERSION_NO=BD.VERSION_NO AND QR.BREAK_POINT=BD.WEIGHT_BREAK_SLAB))
	           LOOP
             		  INSERT INTO QMS_QUOTE_RATES
        (Quote_Id,
         Sell_Buy_Flag,
         Sellrate_Id,
         Buyrate_Id,
         Rate_Lane_No,
         Version_No,
         Charge_Description,
         Margin_Discount_Flag,
         Margin_Type,
         Margin,
         Discount_type,
         Discount,
         Break_Point,
         Line_No,
         Buy_Rate,
         R_SELL_RATE,
         Charge_At,
         Serial_No,
         Id,
         Frequency,
         Carrier,
         Transit_Time,
         Rate_Validity,
         Frequency_checked,
         Carrier_Checked,
         Transit_checked,
         Validity_checked
      )
        SELECT p_Newuniqueid,
               Qu.Sell_Buy_Flag,
               Qu.New_Sellcharge_Id,
               Qu.New_Buycharge_Id,
               Qu.New_Lane_No,
               Qu.New_Version_No,
               V_charge_description,
               V_Margin_Discount_Flag,
               V_Margin_Type,
               V_Margin,
               V_Discount_type,
               V_Discount,
               J.WEIGHT_BREAK_SLAB,
              v_count,
               J.Chargerate,
               (SELECT chargerate FROM QMS_REC_CON_SELLRATESDTL WHERE buyrateid=Bd.Buyrateid AND lane_no=Bd.Lane_No AND weightbreakslab=Bd.Weight_Break_Slab AND version_no=Bd.Version_No AND ai_flag='A'),
               v_charge_at,
               v_serial_no,
               Seq_Qms_Quote_Rates.NEXTVAL,
               J.FREQUENCY,
               J.Carrier_Id,
               J.Transit_Time,
               J.Valid_Upto,
               V_frequency_checked,
               V_carrier_checked,
               V_transit_checked,
               v_Validity_Checked
          FROM QMS_QUOTES_UPDATED Qu, QMS_BUYRATES_DTL Bd
         WHERE Qu.New_Buycharge_Id = Bd.Buyrateid

           AND Qu.New_Lane_No = Bd.Lane_No
           AND Qu.New_version_no=Bd.version_no
           AND Qu.New_version_no=(SELECT new_version_no FROM QMS_QUOTES_UPDATED WHERE quote_id=p_Quoteid AND changedesc=p_Changedesc and confirm_flag is null)--confirm flag @@Added by kiran.v
           AND Bd.Weight_Break_Slab=J.Weight_Break_Slab
           AND Qu.Quote_Id = p_Quoteid
           AND Qu.Changedesc = p_Changedesc
           AND  Qu.Old_Buycharge_Id=bd.buyrateid
           AND   Qu.Old_Lane_No=bd.lane_no;
         v_count := v_count+1;
             END LOOP;
            EXCEPTION WHEN NO_DATA_FOUND THEN
            DBMS_OUTPUT.put_line('No Extra charages are there for not list..');
            END;

           END IF;
      --@@Ended by subrahmanyam for 188024 on 05-Nov-09

      /*For i In (Select Qr.Break_Point,
                       Qr.Margin_Type,
                       Qr.Margin,
                       Qu.New_Buycharge_Id,
                       Qu.New_Lane_No,
                       Qr.Frequency_Checked,
                       Qr.Carrier_Checked,
                       Qr.Transit_Checked,
                       Qr.Validity_Checked

                  From Qms_Quote_Rates Qr, Qms_Quotes_Updated Qu
                 Where Decode(Qr.Sell_Buy_Flag,
                              'RSR',
                              Decode(Qr.Margin_Discount_Flag, 'M', 'BR'),
                              'CSR',
                              Decode(Qr.Margin_Discount_Flag, 'M', 'BR'),
                              Qr.Sell_Buy_Flag) = Qu.Sell_Buy_Flag
                        And Qr.Buyrate_Id = Qu.Old_Buycharge_Id
                   And Qr.Rate_Lane_No = Qu.Old_Lane_No
                   And Qu.Quote_Id = Qr.Quote_Id
                   And Qu.Quote_Id = p_Quoteid
                   And Qu.Changedesc = p_Changedesc) Loop

        Update Qms_Quote_Rates
           Set Margin_Type = i.Margin_Type, Margin = i.Margin , Frequency_Checked = i.frequency_checked,
         Carrier_checked = i.carrier_checked,
         Transit_checked = i.transit_checked,
         Validity_Checked=i.validity_checked
        --Set Margin_Type=i.Margin_Type,=Decode(Break_Point,iMargin .Break_Point,i.Margin,'0.0')
         Where Quote_Id = p_Newuniqueid
           And Break_Point = i.Break_Point --@@Modified by Kameswari for the WPBN issue-138677
           And Sell_Buy_Flag = p_Sellbuyflag
           And Buyrate_Id = i.New_Buycharge_Id
           And Rate_Lane_No = i.New_Lane_No;
         count :=count+1;
      End Loop;*/

      --@@Added by kameswari for Surcharge Enhancements
      /* select max(Line_no) into v_lineNo from qms_quote_rates qr,qms_quotes_updated qu where qu.quote_id= p_Quoteid and qr.quote_id=qu.quote_id and qr.sell_buy_flag='BR'
      and qr.rate_lane_no=qu.old_lane_no and qr.Buyrate_Id = qu.Old_Buycharge_Id;

      select max(Line_no) into v_newLineno from  qms_quote_rates qr,qms_quotes_updated qu where  qr.quote_id=p_Newuniqueid and qr.sell_buy_flag='BR'
      and qr.rate_lane_no=qu.new_lane_no and qr.Buyrate_Id = qu.New_Buycharge_Id;

       If v_newLineno>v_lineNo
       Then
       for k In(select  line_no from qms_quote_rates  qr,qms_quotes_updated qu where  qr.quote_id=p_Newuniqueid and qr.sell_buy_flag='BR'
      and qr.rate_lane_no=qu.new_lane_no and qr.Buyrate_Id = qu.old_Buycharge_Id and line_no>v_LineNo)
      Loop
       Update qms_quote_rates set Margin='0.0' where line_no=k.line_no;
       End Loop;
       End If;*/
      /*Update qms_quote_rates
         set Margin = '0.0'
       where Quote_Id = p_Newuniqueid
         And Sell_Buy_Flag = p_Sellbuyflag
         And Buyrate_Id =
             (select distinct buyrate_id
                from qms_quote_rates qr, qms_quotes_updated qu
               where qr.quote_id = p_Newuniqueid
                 and qr.sell_buy_flag = 'BR'
                 and qr.rate_lane_no = qu.new_lane_no
                 and qr.Buyrate_Id = qu.New_Buycharge_Id)
         and line_no not in
             (select line_no
                from qms_quote_rates qr, qms_quotes_updated qu
               where qu.quote_id = p_Quoteid
                 and qr.quote_id = qu.quote_id
                 and qr.sell_buy_flag = 'BR'
                 and qr.rate_lane_no = qu.old_lane_no
                 and qr.Buyrate_Id = qu.Old_Buycharge_Id);*/

      --@@Enhancements

    /*Elsif (Upper(p_Sellbuyflag) = 'RSR' Or Upper(p_Sellbuyflag) = 'CSR') Then
      Insert Into Qms_Quote_Rates
        (Quote_Id,
         Sell_Buy_Flag,
         Sellrate_Id,
         Buyrate_Id,
         Rate_Lane_No,
         Version_No, --@@Added for the WPBN issues-146448,146968 on 19/12/08
         Charge_Description,
         Margin_Discount_Flag,
         Margin_Type,
         Margin,
         Discount_Type,
         Discount,
         Break_Point,
         Line_No,
         Buy_Rate,
         R_SELL_RATE,
         Charge_At,
         Serial_No,
         Id,
         Frequency,
         Carrier,
         Transit_Time,
         Rate_Validity,
         Frequency_checked,
         Carrier_Checked,
         Transit_checked,
         Validity_checked)
        Select p_Newuniqueid,
              Qu.Sell_Buy_Flag,
               Qu.New_Sellcharge_Id,
               Qu.New_Buycharge_Id,
               Qu.New_Lane_No,
               Qu.New_Version_No,--@@Added for the WPBN issues-146448,146968 on 19/12/08
               (Select Distinct Iqr.Charge_Description
                  From Qms_Quote_Rates Iqr
                 Where Iqr.Quote_Id = Qu.Quote_Id
                   And Iqr.Sell_Buy_Flag = Qu.Sell_Buy_Flag
                   And Iqr.Sellrate_Id = Qu.Old_Sellcharge_Id
                   And Iqr.Buyrate_Id = Qu.Old_Buycharge_Id
                   And Iqr.Rate_Lane_No = Qu.Old_Lane_No) Charge_Desc,
               (Select Distinct Iqr.Margin_Discount_Flag
                  From Qms_Quote_Rates Iqr
                 Where Iqr.Quote_Id = Qu.Quote_Id
                   And Iqr.Sell_Buy_Flag = Qu.Sell_Buy_Flag
                   And Iqr.Sellrate_Id = Qu.Old_Sellcharge_Id
                   And Iqr.Buyrate_Id = Qu.Old_Buycharge_Id
                   And Iqr.Rate_Lane_No = Qu.Old_Lane_No) Margin_Discount_Flag,
                 Qr.Charge_Description,
                 Qr.Margin_Discount_Flag,
               Qr.Margin_Type,
               Qr.Margin,
               Decode(Sd.Rate_Description,
                      'A FREIGHT RATE',
                      v_Discountforfreight,
                      null,
                      v_Discountforfreight,
                      0.0) v_Discountforfreight,
                     -- v_Discountforfreight,
               Qr.Discount_Type,
               Qr.Discount,
               Qr.break_point,
               --Sd.Line_No,
               Qr.Line_No,
               Sd.Buy_Rate_Amt,
               Sd.Chargerate,
               (Select Distinct Iqr.Charge_At
                  From Qms_Quote_Rates Iqr
                 Where Iqr.Quote_Id = Qu.Quote_Id
                   And Iqr.Sell_Buy_Flag = Qu.Sell_Buy_Flag
                   And Iqr.Sellrate_Id = Qu.Old_Sellcharge_Id
                   And Iqr.Buyrate_Id = Qu.Old_Buycharge_Id
                   And Iqr.Rate_Lane_No = Qu.Old_Lane_No) Charge_At,
                   Qr.charge_at,
               (Select Distinct Iqr.Serial_No
                  From Qms_Quote_Rates Iqr
                 Where Iqr.Quote_Id = Qu.Quote_Id
                   And Iqr.Sell_Buy_Flag = Qu.Sell_Buy_Flag
                   And Iqr.Sellrate_Id = Qu.Old_Sellcharge_Id
                   And Iqr.Buyrate_Id = Qu.Old_Buycharge_Id
                   And Iqr.Rate_Lane_No = Qu.Old_Lane_No) Serial_No,
                   Qr.Serial_No,
               Sd.Rate_Description,
            Sd.Frequency,
            Sd.Transit_Time,
           Qr.Carrier,
            Bd.Valid_Upto,
            Qr.Frequency_Checked,
               Qr.Carrier_Checked,
               Qr.Transit_Checked,
               Qr.Validity_Checked
          From Qms_Quotes_Updated Qu, Qms_Rec_Con_Sellratesdtl Sd,Qms_Buyrates_Dtl Bd,Qms_Quote_Rates Qr
         Where Qu.New_Sellcharge_Id = Sd.Rec_Con_Id
           And Qu.New_Buycharge_Id = Sd.Buyrateid
          And Qu.Quote_id = Qr.Quote_Id                 --@@Added for the WPBN issue-146968 on 02/01/09
         And Bd.Buyrateid=Qr.Buyrate_Id
         And Qr.Sellrate_Id=Sd.Rec_Con_Id
         ANd Bd.Lane_No=Qr.Rate_Lane_No
         And Bd.Weight_Break_Slab=Qr.Break_Point
           And Sd.Buyrateid=Bd.Buyrateid
           And Sd.Version_No=Bd.Version_No
           And Sd.Lane_No=Bd.Lane_No
           And Qu.New_Lane_No = Sd.Lane_No
         And Qu.New_version_no=(select new_version_no from qms_quotes_updated where quote_id=p_Quoteid and changedesc=p_Changedesc)
           And Qu.New_Version_No=Sd.Version_No--@@Added by kameswari for the WPBN issue-146448 on 24/12/08
         And Qu.Quote_Id = p_Quoteid
           And Qu.Changedesc = p_Changedesc;*/
     /* For i In (Select Qr.Break_Point,
                       Qr.Discount_Type,
                       Qr.Discount,
                       Qu.New_Sellcharge_Id,
                       Qu.New_Buycharge_Id,
                       Qu.New_Lane_No,
                       Qr.Frequency_Checked,
                       Qr.Carrier_Checked,
                       Qr.Transit_Checked,
                       Qr.Validity_Checked
                  From Qms_Quote_Rates Qr, Qms_Quotes_Updated Qu
                 Where Qr.Sell_Buy_Flag = Qu.Sell_Buy_Flag
                   And Qr.Sellrate_Id = Qu.Old_Sellcharge_Id
                   And Qr.Buyrate_Id = Qu.Old_Buycharge_Id
                   And Qr.Rate_Lane_No = Qu.Old_Lane_No
                   And Qu.Quote_Id = Qr.Quote_Id
                   And Qu.Quote_Id = p_Quoteid
                   And Qu.Changedesc = p_Changedesc) Loop
        Update Qms_Quote_Rates
           Set Discount_Type = i.Discount_Type, Discount = i.Discount,Frequency_Checked=i.frequency_checked,
           Carrier_Checked=i.carrier_checked,Transit_Checked=i.transit_checked,Validity_Checked=i.validity_checked
         Where Quote_Id = p_Newuniqueid
           And Break_Point = i.Break_Point
           And Sell_Buy_Flag = p_Sellbuyflag
           And Sellrate_Id = i.New_Sellcharge_Id
           And Buyrate_Id = i.New_Buycharge_Id
           And Rate_Lane_No = i.New_Lane_No;
      End Loop;*/
    ELSIF (UPPER(p_Sellbuyflag) = 'B') THEN
      INSERT INTO QMS_QUOTE_RATES
        (Quote_Id,
         Sell_Buy_Flag,
         Buyrate_Id,
         Charge_Id,
         Charge_Description,
         Margin_Discount_Flag,
         Margin_Type,
         Margin,
         Break_Point,
         Line_No,
         Buy_Rate,
         Charge_At,
         Id)
        SELECT p_Newuniqueid,
               Qu.Sell_Buy_Flag,
               Qu.New_Buycharge_Id,
               (SELECT DISTINCT Iqr.Charge_Id
                  FROM QMS_QUOTE_RATES Iqr
                 WHERE Iqr.Quote_Id = Qu.Quote_Id
                   AND DECODE(Iqr.Sell_Buy_Flag,
                              'S',
                              DECODE(Iqr.Margin_Discount_Flag, 'M', 'B'),
                              Iqr.Sell_Buy_Flag) = Qu.Sell_Buy_Flag
                   AND Iqr.Buyrate_Id = Qu.Old_Buycharge_Id) Charge_Id,
               (SELECT DISTINCT Iqr.Charge_Description
                  FROM QMS_QUOTE_RATES Iqr
                 WHERE Iqr.Quote_Id = Qu.Quote_Id
                   AND DECODE(Iqr.Sell_Buy_Flag,
                              'S',
                              DECODE(Iqr.Margin_Discount_Flag, 'M', 'B'),
                              Iqr.Sell_Buy_Flag) = Qu.Sell_Buy_Flag --@@Added  by Kameswari for the WPBN issue-129343
                   AND Iqr.Buyrate_Id = Qu.Old_Buycharge_Id
                   AND Iqr.Charge_Description IN
                       (SELECT chargedescid
                          FROM QMS_CHARGE_GROUPSMASTER
                         WHERE chargegroup_id IN
                               (SELECT chargegroupid
                                  FROM QMS_QUOTE_CHARGEGROUPDTL
                                 WHERE quote_id = p_Quoteid)
                           AND inactivate = 'N')) Charge_Desc,
               (SELECT DISTINCT Iqr.Margin_Discount_Flag
                  FROM QMS_QUOTE_RATES Iqr
                 WHERE Iqr.Quote_Id = Qu.Quote_Id
                   AND DECODE(Iqr.Sell_Buy_Flag,
                              'S',
                              DECODE(Iqr.Margin_Discount_Flag, 'M', 'B'),
                              Iqr.Sell_Buy_Flag) = Qu.Sell_Buy_Flag
                     AND Iqr.Buyrate_Id = Qu.Old_Buycharge_Id) Margin_Discount_Flag,
               'P',
               v_Marginforcharges,
               Bd.Chargeslab,
               Bd.Lane_No,
               Bd.Chargerate,
               (SELECT DISTINCT Iqr.Charge_At
                  FROM QMS_QUOTE_RATES Iqr
                 WHERE Iqr.Quote_Id = Qu.Quote_Id
                   AND DECODE(Iqr.Sell_Buy_Flag,
                              'S',
                              DECODE(Iqr.Margin_Discount_Flag, 'M', 'B'),
                              Iqr.Sell_Buy_Flag) = Qu.Sell_Buy_Flag
                   AND Iqr.Buyrate_Id = Qu.Old_Buycharge_Id) Charge_At,
               Seq_Qms_Quote_Rates.NEXTVAL
          FROM QMS_QUOTES_UPDATED Qu, QMS_BUYCHARGESDTL Bd
         WHERE Qu.New_Buycharge_Id = Bd.Buysellchaegeid
           AND Qu.Quote_Id = p_Quoteid
           AND Qu.Changedesc = p_Changedesc;
 ---aDDED BY GOVIND FOR NOT GETTING THE SALESPERSON MARGIN WHEN BC OR SC IS CHANGED
   SELECT COUNT(1) INTO V_SLAB_COUNT FROM
(SELECT QR.BREAK_POINT
                  FROM QMS_QUOTE_RATES Qr, QMS_QUOTES_UPDATED Qu
                 WHERE DECODE(Qr.Sell_Buy_Flag,
                              'S',
                              DECODE(Qr.Margin_Discount_Flag, 'M', 'B'),
                              Qr.Sell_Buy_Flag) = Qu.Sell_Buy_Flag
                   AND Qr.Buyrate_Id = Qu.Old_Buycharge_Id
                   AND Qu.Quote_Id = Qr.Quote_Id
                   AND Qu.Quote_Id = p_quoteid
                   AND Qu.Changedesc = p_changedesc
                   AND EXISTS (SELECT     Bd.Chargeslab

          FROM QMS_QUOTES_UPDATED Qu, QMS_BUYCHARGESDTL Bd
         WHERE Qu.New_Buycharge_Id = Bd.Buysellchaegeid
           AND Qu.Quote_Id = p_Quoteid
           AND Qu.Changedesc = p_changedesc
           AND BD.CHARGESLAB IN (QR.BREAK_POINT))) ;
    IF(V_SLAB_COUNT >0)  THEN
      FOR i IN (SELECT Qr.Break_Point,
                       Qr.Margin_Type,
                       Qr.Margin,
                       Qu.New_Buycharge_Id
                  FROM QMS_QUOTE_RATES Qr, QMS_QUOTES_UPDATED Qu
                 WHERE DECODE(Qr.Sell_Buy_Flag,
                              'S',
                              DECODE(Qr.Margin_Discount_Flag, 'M', 'B'),
                              Qr.Sell_Buy_Flag) = Qu.Sell_Buy_Flag
                   AND Qr.Buyrate_Id = Qu.Old_Buycharge_Id
                   AND Qu.Quote_Id = Qr.Quote_Id
                   AND Qu.Quote_Id = p_Quoteid
                   AND Qu.Changedesc = p_Changedesc) LOOP
        UPDATE QMS_QUOTE_RATES
           SET Margin_Type = i.Margin_Type, Margin = i.Margin
         WHERE Quote_Id = p_Newuniqueid
           AND Break_Point = i.Break_Point
           AND Sell_Buy_Flag = p_Sellbuyflag
           AND Buyrate_Id = i.New_Buycharge_Id;
      END LOOP;
      ELSE
      UPDATE QMS_QUOTE_RATES
          SET Margin = '0.00000',
               Discount = '0.00000'
         WHERE Quote_Id = p_Newuniqueid
         AND Sell_Buy_Flag = p_Sellbuyflag;
       END IF;
       --- GOVINDS ENDS HERE
    ELSIF (UPPER(p_Sellbuyflag) = 'S') THEN
      INSERT INTO QMS_QUOTE_RATES
        (Quote_Id,
         Sell_Buy_Flag,
         Sellrate_Id,
         Buyrate_Id,
         Charge_Id,
         Charge_Description,
         Margin_Discount_Flag,
         Margin_type,
         Margin,
         Discount_Type,
         Discount,
         Break_Point,
         Line_No,
         Buy_Rate,
         R_SELL_RATE,
         Charge_At,
         Id)
        SELECT p_Newuniqueid,
               Qu.Sell_Buy_Flag,
               Qu.New_Sellcharge_Id,
               Qu.New_Buycharge_Id,
               (SELECT DISTINCT Iqr.Charge_Id
                  FROM QMS_QUOTE_RATES Iqr
                 WHERE Iqr.Quote_Id = Qu.Quote_Id
                      /*AND DECODE (iqr.sell_buy_flag,
                         'S', DECODE (iqr.margin_discount_flag,
                            'M', 'B','S'),
                                iqr.sell_buy_flag

                                  ) = qu.sell_buy_flag*/

                   AND Iqr.Sell_Buy_Flag = Qu.Sell_Buy_Flag
                   AND Iqr.Buyrate_Id = Qu.Old_Buycharge_Id
                   AND Iqr.Sellrate_Id = Qu.Old_Sellcharge_Id) Charge_Id,
               (SELECT DISTINCT Iqr.Charge_Description
                  FROM QMS_QUOTE_RATES Iqr
                 WHERE Iqr.Quote_Id = Qu.Quote_Id
                   AND Iqr.Sell_Buy_Flag = Qu.Sell_Buy_Flag
                   AND Iqr.Sellrate_Id = Qu.Old_Sellcharge_Id --@@Added by Kameswari for the WPBN issue-129343
                   AND Iqr.Buyrate_Id = Qu.Old_Buycharge_Id
                   AND Iqr.Charge_Description IN
                       (SELECT chargedescid
                          FROM QMS_CHARGE_GROUPSMASTER
                         WHERE chargegroup_id IN
                               (SELECT chargegroupid
                                  FROM QMS_QUOTE_CHARGEGROUPDTL
                                 WHERE quote_id = p_Quoteid)
                           AND inactivate = 'N')) Charge_Desc,
               (SELECT DISTINCT Iqr.Margin_Discount_Flag
                  FROM QMS_QUOTE_RATES Iqr
                 WHERE Iqr.Quote_Id = Qu.Quote_Id
                   AND Iqr.Sell_Buy_Flag = Qu.Sell_Buy_Flag
                   AND Iqr.Sellrate_Id = Qu.Old_Sellcharge_Id
                   AND Iqr.Buyrate_Id = Qu.Old_Buycharge_Id) Margin_Discount_Flag,
               'P',
               v_Marginforcharges,
               'P',
               v_Discountforcharges,
               Sd.Chargeslab,
               Sd.Lane_No,
               Sd.Chargerate,
               DECODE(Sm.Margin_Type,
                      'A',
                      Sd.Chargerate + Sd.Marginvalue,
                      (Sd.Chargerate + Sd.Marginvalue * Sd.Chargerate * 0.01)) R_SELL_RATE,
               (SELECT DISTINCT Iqr.Charge_At
                  FROM QMS_QUOTE_RATES Iqr
                 WHERE Iqr.Quote_Id = Qu.Quote_Id
                   AND Iqr.Sell_Buy_Flag = Qu.Sell_Buy_Flag
                   AND Iqr.Sellrate_Id = Qu.Old_Sellcharge_Id
                   AND Iqr.Buyrate_Id = Qu.Old_Buycharge_Id) Charge_At,
               Seq_Qms_Quote_Rates.NEXTVAL
          FROM QMS_QUOTES_UPDATED    Qu,
               QMS_SELLCHARGESDTL    Sd,
               QMS_SELLCHARGESMASTER Sm
         WHERE Qu.New_Sellcharge_Id = Sd.Sellchargeid
           AND Sm.Sellchargeid = Sd.Sellchargeid
           AND Qu.New_Buycharge_Id = Sm.Buychargeid
           AND Qu.Quote_Id = p_Quoteid
           AND Qu.Changedesc = p_Changedesc;
   ---aDDED BY GOVIND FOR NOT GETTING THE SALESPERSON MARGIN WHEN BC OR SC IS CHANGED
    SELECT COUNT(1) INTO V_SLAB_COUNT FROM
(SELECT QR.BREAK_POINT
                  FROM QMS_QUOTE_RATES Qr, QMS_QUOTES_UPDATED Qu
                 WHERE DECODE(Qr.Sell_Buy_Flag,
                              'S',
                              DECODE(Qr.Margin_Discount_Flag, 'M', 'B'),
                              Qr.Sell_Buy_Flag) = Qu.Sell_Buy_Flag
                   AND Qr.Buyrate_Id = Qu.Old_Buycharge_Id
                   AND Qu.Quote_Id = Qr.Quote_Id
                   AND Qu.Quote_Id = p_quoteid
                   AND Qu.Changedesc = p_changedesc
                   AND EXISTS (SELECT     Bd.Chargeslab

          FROM QMS_QUOTES_UPDATED Qu, QMS_BUYCHARGESDTL Bd
         WHERE Qu.New_Buycharge_Id = Bd.Buysellchaegeid
           AND Qu.Quote_Id = p_Quoteid
           AND Qu.Changedesc = p_changedesc
           AND BD.CHARGESLAB IN (QR.BREAK_POINT))) ;
    IF V_SLAB_COUNT>0  THEN

      FOR i IN (SELECT Qr.Break_Point,
                       Qr.Margin_Type,
                       Qr.Margin,
                       Qr.Discount_Type,
                       Qr.Discount,
                       Qu.New_Sellcharge_Id,
                       Qu.New_Buycharge_Id
                  FROM QMS_QUOTE_RATES Qr, QMS_QUOTES_UPDATED Qu
                 WHERE Qr.Sell_Buy_Flag = Qu.Sell_Buy_Flag
                   AND Qr.Sellrate_Id = Qu.Old_Sellcharge_Id
                   AND Qr.Buyrate_Id = Qu.Old_Buycharge_Id
                   AND Qu.Quote_Id = Qr.Quote_Id
                   AND Qu.Quote_Id = p_Quoteid
                   AND Qu.Changedesc = p_Changedesc) LOOP
        UPDATE QMS_QUOTE_RATES
           SET Margin_Type=i.margin_type,Margin=i.margin,
           Discount_Type = i.Discount_Type, Discount = i.Discount
         WHERE Quote_Id = p_Newuniqueid
           AND Break_Point = i.Break_Point
           AND Sell_Buy_Flag = p_Sellbuyflag
           AND Sellrate_Id = i.New_Sellcharge_Id
           AND Buyrate_Id = i.New_Buycharge_Id;
      END LOOP;
      ELSE
      UPDATE QMS_QUOTE_RATES
           SET Margin = '0.00000',
               Discount = '0.00000'
         WHERE Quote_Id = p_Newuniqueid
         AND Sell_Buy_Flag = p_Sellbuyflag;

      END IF;
      ---- GOVIND ENDS HERE
    ELSIF (UPPER(p_Sellbuyflag) = 'BC') THEN
      INSERT INTO QMS_QUOTE_RATES
        (Quote_Id,
         Sell_Buy_Flag,
         Buyrate_Id,
         Charge_Description,
         Margin_Discount_Flag,
         Margin_Type,
         Margin,
         Break_Point,
         Line_No,
         Buy_Rate,
         Charge_At,
         Id)
        SELECT p_Newuniqueid,
               Qu.Sell_Buy_Flag,
               Qu.New_Buycharge_Id,
               (SELECT DISTINCT Iqr.Charge_Description
                  FROM QMS_QUOTE_RATES Iqr
                 WHERE Iqr.Quote_Id = Qu.Quote_Id
                   AND DECODE(Iqr.Sell_Buy_Flag,
                              'SC',
                              DECODE(Iqr.Margin_Discount_Flag, 'M', 'BC'),
                              Iqr.Sell_Buy_Flag) = Qu.Sell_Buy_Flag
                   AND Iqr.Buyrate_Id = Qu.Old_Buycharge_Id
                   AND Iqr.Charge_At = Qu.Charge_At) Charge_Desc,
               (SELECT DISTINCT Iqr.Margin_Discount_Flag
                  FROM QMS_QUOTE_RATES Iqr
                 WHERE Iqr.Quote_Id = Qu.Quote_Id
                   AND DECODE(Iqr.Sell_Buy_Flag,
                              'SC',
                              DECODE(Iqr.Margin_Discount_Flag, 'M', 'BC'),
                              Iqr.Sell_Buy_Flag) = Qu.Sell_Buy_Flag
                   AND Iqr.Buyrate_Id = Qu.Old_Buycharge_Id
                   AND Iqr.Charge_At = Qu.Charge_At) Margin_Discount_Flag,
               'P',
               v_Marginforcartage,
               Bd.Chargeslab,
               Bd.Line_No,
               Bd.Chargerate,
               (SELECT DISTINCT Iqr.Charge_At
                  FROM QMS_QUOTE_RATES Iqr
                 WHERE Iqr.Quote_Id = Qu.Quote_Id
                   AND DECODE(Iqr.Sell_Buy_Flag,
                              'SC',
                              DECODE(Iqr.Margin_Discount_Flag, 'M', 'BC'),
                              Iqr.Sell_Buy_Flag) = Qu.Sell_Buy_Flag
                   AND Iqr.Buyrate_Id = Qu.Old_Buycharge_Id
                   AND Iqr.Charge_At = Qu.Charge_At) Charge_At,
               Seq_Qms_Quote_Rates.NEXTVAL
          FROM QMS_QUOTES_UPDATED Qu, QMS_CARTAGE_BUYDTL Bd
         WHERE Qu.New_Buycharge_Id = Bd.Cartage_Id
           AND Qu.Zone_Code = Bd.Zone_Code
           AND Qu.Charge_At = Bd.Charge_Type
           AND Qu.Quote_Id = p_Quoteid
           AND Qu.Changedesc = p_Changedesc;
      FOR i IN (SELECT Qr.Break_Point,
                       Qr.Margin_Type,
                       Qr.Margin,
                       Qu.New_Buycharge_Id,
                       Qu.Charge_At
                  FROM QMS_QUOTE_RATES    Qr,
                       QMS_QUOTE_MASTER   Qm,
                       QMS_QUOTES_UPDATED Qu
                 WHERE DECODE(Qr.Sell_Buy_Flag,
                              'SC',
                              DECODE(Qr.Margin_Discount_Flag, 'M', 'BC'),
                              Qr.Sell_Buy_Flag) = Qu.Sell_Buy_Flag
                   AND Qr.Buyrate_Id = Qu.Old_Buycharge_Id
                   AND Qu.Quote_Id = Qr.Quote_Id
                   AND Qu.Quote_Id = Qm.Id
                   AND Qu.Zone_Code =
                       DECODE(Qr.Charge_At,
                              'Pickup',
                              Qm.Shipperzones,
                              'Delivery',
                              Qm.Consigneezones)
                   AND Qu.Charge_At = Qr.Charge_At
                   AND Qu.Quote_Id = p_Quoteid
                   AND Qu.Changedesc = p_Changedesc) LOOP
        UPDATE QMS_QUOTE_RATES
           SET Margin_Type = i.Margin_Type, Margin = i.Margin
         WHERE Quote_Id = p_Newuniqueid
           AND Break_Point = i.Break_Point
           AND Sell_Buy_Flag = p_Sellbuyflag
           AND Buyrate_Id = i.New_Buycharge_Id
           AND Charge_At = i.Charge_At;
      END LOOP;
       ELSIF (UPPER(p_Sellbuyflag) = 'SC') THEN
      INSERT INTO QMS_QUOTE_RATES
        (Quote_Id,
         Sell_Buy_Flag,
         Sellrate_Id,
         Buyrate_Id,
         Charge_Description,
         Margin_Discount_Flag,
         Margin_type,
         Margin,
         Discount_Type,
         Discount,
         Break_Point,
         Line_No,
         Buy_Rate,
         R_SELL_RATE,
         Charge_At,
         Id)
        SELECT p_Newuniqueid,
               Qu.Sell_Buy_Flag,
               Qu.New_Sellcharge_Id,
               Qu.New_Buycharge_Id,
               (SELECT DISTINCT Iqr.Charge_Description
                  FROM QMS_QUOTE_RATES Iqr
                 WHERE Iqr.Quote_Id = Qu.Quote_Id
                   AND Iqr.Sell_Buy_Flag = Qu.Sell_Buy_Flag
                   AND Iqr.Sellrate_Id = Qu.Old_Sellcharge_Id
                   AND Iqr.Buyrate_Id = Qu.Old_Buycharge_Id
                   AND Iqr.Charge_At = Qu.Charge_At) Charge_Desc,
               (SELECT DISTINCT Iqr.Margin_Discount_Flag
                  FROM QMS_QUOTE_RATES Iqr
                 WHERE Iqr.Quote_Id = Qu.Quote_Id
                   AND Iqr.Sell_Buy_Flag = Qu.Sell_Buy_Flag
                   AND Iqr.Sellrate_Id = Qu.Old_Sellcharge_Id
                   AND Iqr.Buyrate_Id = Qu.Old_Buycharge_Id
                   AND Iqr.Charge_At = Qu.Charge_At) Margin_Discount_Flag,
               'P',
               v_Marginforcartage,
               'P',
               v_Discountforcartage,
               Sd.Chargeslab,
               Sd.Line_No,
               Sd.Buyrate_Amt,
               Sd.Chargerate,
               (SELECT DISTINCT Iqr.Charge_At
                  FROM QMS_QUOTE_RATES Iqr
                 WHERE Iqr.Quote_Id = Qu.Quote_Id
                   AND Iqr.Sell_Buy_Flag = Qu.Sell_Buy_Flag
                   AND Iqr.Sellrate_Id = Qu.Old_Sellcharge_Id
                   AND Iqr.Buyrate_Id = Qu.Old_Buycharge_Id
                   AND Iqr.Charge_At = Qu.Charge_At) Charge_At,
               Seq_Qms_Quote_Rates.NEXTVAL
          FROM QMS_QUOTES_UPDATED Qu, QMS_CARTAGE_SELLDTL Sd
         WHERE Qu.New_Buycharge_Id = Sd.Cartage_Id
           AND Qu.New_Sellcharge_Id = Sd.Sell_Cartage_Id
           AND Qu.Zone_Code = Sd.Zone_Code
           AND Qu.Charge_At = Sd.Charge_Type
           AND Sd.Activeinactive = 'A'
           AND Qu.Quote_Id = p_Quoteid
           AND Qu.Changedesc = p_Changedesc;
      FOR i IN (SELECT Qr.Break_Point,
                      Qr.Margin_Type,
                      Qr.Margin,
                       Qr.Discount_Type,
                       Qr.Discount,
                       Qu.New_Sellcharge_Id,
                       Qu.New_Buycharge_Id,
                       Qu.Charge_At
                  FROM QMS_QUOTE_RATES    Qr,
                       QMS_QUOTE_MASTER   Qm,
                       QMS_QUOTES_UPDATED Qu
                 WHERE Qr.Sell_Buy_Flag = Qu.Sell_Buy_Flag
                   AND Qr.Buyrate_Id = Qu.Old_Buycharge_Id
                   AND Qr.Sellrate_Id = Qu.Old_Sellcharge_Id
                   AND Qu.Quote_Id = Qr.Quote_Id
                   AND Qu.Quote_Id = Qm.Id
                   AND Qu.Zone_Code =
                       DECODE(Qr.Charge_At,
                              'Pickup',
                              Qm.Shipperzones,
                              'Delivery',
                              Qm.Consigneezones)
                   AND Qu.Charge_At = Qr.Charge_At
                   AND Qu.Quote_Id = p_Quoteid
                   AND Qu.Changedesc = p_Changedesc) LOOP
        UPDATE QMS_QUOTE_RATES
           SET Margin_Type=i.margin_type,Margin=i.margin,
           Discount_Type = i.Discount_Type, Discount = i.Discount
         WHERE Quote_Id = p_Newuniqueid
           AND Break_Point = i.Break_Point
           AND Sell_Buy_Flag = p_Sellbuyflag
           AND Buyrate_Id = i.New_Buycharge_Id
           AND Sellrate_Id = i.New_Sellcharge_Id
           AND Charge_At = i.Charge_At;
      END LOOP;
    END IF;
    --@@Added by kameswari for the WPBN issue-154398 on 19/02/09
     IF UPPER(p_Sellbuyflag) = 'B'
     THEN
        SELECT  qu.new_buycharge_id,qu.old_buycharge_id INTO v_newchargeid,v_oldchargeid FROM  QMS_QUOTES_UPDATED qu WHERE qu.sell_buy_flag='B' AND qu.quote_id=p_Quoteid AND qu.changedesc=p_Changedesc;
         SELECT qbm.chargebasis,qbm.rate_break INTO v_oldchargebasis,v_oldwtbreak FROM QMS_BUYSELLCHARGESMASTER qbm   WHERE qbm.buysellchargeid=v_oldchargeid;
         SELECT qbm.chargebasis,qbm.rate_break INTO v_newchargebasis,v_newwtbreak FROM QMS_BUYSELLCHARGESMASTER qbm   WHERE qbm.buysellchargeid=v_newchargeid;
       IF(v_oldchargebasis<>v_newchargebasis OR v_oldwtbreak<>v_newwtbreak)
       THEN
           UPDATE QMS_QUOTE_RATES SET margin='0.0',discount='0.0' WHERE buyrate_id=v_newchargeid AND sell_buy_flag='B' AND quote_id=p_Quoteid;
       END IF;
     END IF;
        IF UPPER(p_Sellbuyflag) = 'S'
     THEN
        SELECT  qu.new_sellcharge_id,qu.old_sellcharge_id INTO v_newchargeid,v_oldchargeid FROM  QMS_QUOTES_UPDATED qu WHERE qu.sell_buy_flag='S' AND qu.quote_id=p_Quoteid AND qu.changedesc=p_Changedesc;
         SELECT qbm.chargebasis,qbm.rate_break INTO v_oldchargebasis,v_oldwtbreak FROM QMS_SELLCHARGESMASTER qbm   WHERE qbm.sellchargeid=v_oldchargeid;
         SELECT qbm.chargebasis,qbm.rate_break INTO v_newchargebasis,v_newwtbreak FROM QMS_SELLCHARGESMASTER qbm   WHERE qbm.sellchargeid=v_newchargeid;
       IF(v_oldchargebasis<>v_newchargebasis OR v_oldwtbreak<>v_newwtbreak)
       THEN
           UPDATE QMS_QUOTE_RATES SET margin='0.0',discount='0.0' WHERE sellrate_id=v_newchargeid AND sell_buy_flag='S' AND quote_id=p_Quoteid;
       END IF;
     END IF;
     --@@WPBN issue-154398
    UPDATE QMS_QUOTE_MASTER SET Active_Flag = 'I' WHERE Id = p_Quoteid;
    UPDATE QMS_QUOTES_UPDATED UP SET UP.CONFIRM_FLAG = 'C' WHERE UP.QUOTE_ID =p_Quoteid AND UP.CHANGEDESC = p_changedesc;
    /*INSERT INTO FS_USERLOG (LOCATIONID, USERID, DOCTYPE, DOCREFNO, DOCDATE, TRANSACTIONTYPE) VALUES ()*/
    --  update QMS_QUOTE_RATES set


    -- KISHORE

         SELECT QM.IS_MULTI_QUOTE INTO V_ISMULTIQUOTE FROM QMS_QUOTE_MASTER QM WHERE QM.ID=p_Quoteid;

         IF ( V_ISMULTIQUOTE = 'Y') THEN

            FOR J IN ( SELECT QM.QUOTE_ID, QM.ID
                     FROM QMS_QUOTE_MASTER QM
                     WHERE QM.QUOTE_ID =
                      (SELECT QM.QUOTE_ID FROM QMS_QUOTE_MASTER QM WHERE QM.ID = p_Quoteid )
                   AND QM.ID <>  p_Quoteid
                   AND QM.ACTIVE_FLAG='A')

              LOOP

              SELECT Quote_Master_Seq.NEXTVAL INTO p_Newuniqueid1 FROM Dual;
              SELECT Rtplan_Seq.NEXTVAL INTO v_Rt_Plan_Id FROM Dual;

                  INSERT INTO QMS_QUOTE_MASTER
                       (Quote_Id,
                       Shipment_Mode,
                       Prequote_Id,
                       Iu_Flag,
                       Effective_Date,
                       Valid_To,
                       Accept_Validityperiod,
                       Customer_Id,
                       Customer_Addressid,
                       Created_Date,
                       Created_By,
                       Sales_Person,
                       Industry_Id,
                       Commodity_Id,
                       Hazardous_Ind,
                       Un_Number,
                       CLASS,
                       Service_Level_Id,
                       Inco_Terms_Id,
                       Quoting_Station,
                       Origin_Location,
                       Shipper_Zipcode,
                       Origin_Port,
                       Overlength_Cargonotes,
                       Routing_Id,
                       Dest_Location,
                       Consignee_Zipcode,
                       Destionation_Port,
                       Spot_Rates_Flag,
                       Spot_Rate_Type,
                       Email_Flag,
                       Fax_Flag,
                       Print_Flag,
                       Escalated_To,
                       Modified_Date,
                       Modified_By,
                       Terminal_Id,
                       Version_No,
                       Basis,
                       Shipperzones,
                       Consigneezones,
                       Id,
                       Pn_Flag,
                       Update_Flag,
                       Active_Flag,
                       Sent_Flag,
                       Complete_Flag,
                       Quote_Status,
                       Spotbuyrateid,
                       Escalation_Flag,
                       Ie_Flag,
                       Created_Tstmp,
                       Expired_Flag,
                       App_Rej_Tstmp,
                       Cargo_Acc_Type,
                       Cargo_Acc_Place,
                       SHIPPER_MODE,
                       CONSIGNEE_MODE,
                       SHIPPER_CONSOLE_TYPE,
                       CONSIGNEE_CONSOLE_TYPE,
                       MULTI_QUOTE_LANE_NO,
                       MULTI_QUOTE_CARRIER_ID,
                       MULTI_QUOTE_SERVICE_LEVEL,
                       IS_MULTI_QUOTE,
                       MULTI_QUOTE_WEIGHT_BREAK,
                       multi_quote_with,
                       multi_lane_order ,
                       cust_requested_date ,
                       cust_requested_time,
                       status_reason)
                      SELECT p_Newquoteid,
                             Shipment_Mode,
                             Prequote_Id,
                             Iu_Flag,
                             Effective_Date,
                             Valid_To,
                             Accept_Validityperiod,
                             Customer_Id,
                             Customer_Addressid,
                             Created_Date,
                             Created_By,
                             Sales_Person,
                             Industry_Id,
                             Commodity_Id,
                             Hazardous_Ind,
                             Un_Number,
                             CLASS,
                             Service_Level_Id,
                             Inco_Terms_Id,
                             Quoting_Station,
                             Origin_Location,
                             Shipper_Zipcode,
                             Origin_Port,
                             Overlength_Cargonotes,
                             Routing_Id,
                             Dest_Location,
                             Consignee_Zipcode,
                             Destionation_Port,
                             Spot_Rates_Flag,
                             Spot_Rate_Type,
                             Email_Flag,
                             Fax_Flag,
                             Print_Flag,
                             Escalated_To,
                             SYSDATE,
                             p_Modifiedby,
                             Terminal_Id,
                             1,
                             Basis,
                             Shipperzones,
                             Consigneezones,
                             p_Newuniqueid1,
                             Pn_Flag,
                             '',
                             'A',
                             'U',
                             Complete_Flag,
                             --'GEN',
                             Quote_Status, --@@Modified by Kameswari for the WPBN Issue-116548
                             Spotbuyrateid,
                             Escalation_Flag,
                             Ie_Flag,
                             SYSDATE,
                             Expired_Flag,
                             App_Rej_Tstmp,
                             Cargo_Acc_Type,
                             Cargo_Acc_Place,
                             Shipper_Mode,
                             Consignee_Mode,
                             Shipper_Console_Type,
                             Consignee_Console_Type,
                             MULTI_QUOTE_LANE_NO,
                             MULTI_QUOTE_CARRIER_ID,
                             MULTI_QUOTE_SERVICE_LEVEL,
                             IS_MULTI_QUOTE,
                             MULTI_QUOTE_WEIGHT_BREAK,
                             multi_quote_with,
                             multi_lane_order ,
                             cust_requested_date ,
                             cust_requested_time,
                             status_reason
                        FROM QMS_QUOTE_MASTER
                       WHERE Id = J.ID;

  UPDATE QMS_QUOTES_UPDATED UP SET UP.QUOTE_ID = p_Newuniqueid1 WHERE UP.QUOTE_ID =J.ID;


                      INSERT INTO QMS_QUOTE_CHARGEGROUPDTL
                        (Quote_Id, Chargegroupid, Id)
                        SELECT p_Newuniqueid1, Chargegroupid, Seq_Chargegroupdtl_Id.NEXTVAL
                          FROM QMS_QUOTE_CHARGEGROUPDTL
                         WHERE Quote_Id = J.ID;

                      INSERT INTO QMS_QUOTE_NOTES
                        (quote_id, internal_notes, external_notes, id)
                        SELECT p_Newuniqueid1,
                               internal_notes,
                               external_notes,
                               seq_qms_quote_notes.NEXTVAL
                          FROM QMS_QUOTE_NOTES
                         WHERE quote_id = J.ID;

                      INSERT INTO QMS_QUOTE_ATTACHMENTDTL
                        (id, quote_id, attachment_id)
                        SELECT seq_quote_attachdtl_id.NEXTVAL, p_Newuniqueid1, attachment_id
                          FROM QMS_QUOTE_ATTACHMENTDTL
                         WHERE quote_id = J.ID;

                      INSERT INTO QMS_QUOTE_HF_DTL
                        (Quote_Id, Header, Content, Clevel, Align, Id)
                        SELECT p_Newuniqueid1,
                               Header,
                               Content,
                               Clevel,
                               Align,
                               Seq_Qms_Quote_Hf_Dtl.NEXTVAL
                          FROM QMS_QUOTE_HF_DTL
                         WHERE Quote_Id = J.ID;


  FOR I IN (SELECT RTP.RT_PLAN_ID FROM FS_RT_PLAN RTP  WHERE RTP.QUOTE_ID = (SELECT DISTINCT QUOTE_ID FROM QMS_QUOTE_MASTER QMS WHERE QMS.ID=p_Quoteid) )
 LOOP
  SELECT Rtplan_Seq.NEXTVAL INTO v_Rt_Plan_Id FROM Dual;

                      INSERT INTO FS_RT_PLAN
                        (Rt_Plan_Id,
                         Quote_Id,
                         Orig_Trml_Id,
                         Dest_Trml_Id,
                         Orig_Loc_Id,
                         Dest_Loc_Id,
                         Shipper_Id,
                         Consignee_Id,
                         Prmy_Mode,
                         Over_Write_Flag,
                         Shpmnt_Status,
                         Crtd_Timestmp)
                        SELECT v_Rt_Plan_Id,
                               p_Newquoteid,
                               Orig_Trml_Id,
                               Dest_Trml_Id,
                               Orig_Loc_Id,
                               Dest_Loc_Id,
                               Shipper_Id,
                               Consignee_Id,
                               Prmy_Mode,
                               Over_Write_Flag,
                               Shpmnt_Status,
                               Crtd_Timestmp
                          FROM FS_RT_PLAN
                         WHERE RT_PLAN_ID IN (SELECT RP.RT_PLAN_ID
                                  FROM FS_RT_PLAN RP, QMS_QUOTE_MASTER QM
                                 WHERE RP.Quote_Id =  QM.Quote_Id
                                      AND RP.ORIG_LOC_ID = QM.ORIGIN_LOCATION
                                       AND RP.DEST_LOC_ID = QM.DEST_LOCATION
                                       AND  QM.ID= J.ID)
                              AND RT_PLAN_ID = I.RT_PLAN_ID;

                      INSERT INTO FS_RT_LEG
                        (Rt_Plan_Id,
                         Serial_No,
                         Leg_Type,
                         Orig_Loc,
                         Dest_Loc,
                         Shpmnt_Mode,
                         Shpmnt_Status,
                         Auto_Mnul_Flag,
                         Mster_Doc_Id,
                         Leg_Valid_Flag,
                         Pieces_Received,
                         Received_Date,
                         Remarks,
                         Costamount,
                         Orig_Trml_Id,
                         Dest_Trml_Id)
                        SELECT v_Rt_Plan_Id,
                               Serial_No,
                               Leg_Type,
                               Orig_Loc,
                               Dest_Loc,
                               Shpmnt_Mode,
                               Shpmnt_Status,
                               Auto_Mnul_Flag,
                               Mster_Doc_Id,
                               Leg_Valid_Flag,
                               Pieces_Received,
                               Received_Date,
                               Remarks,
                               Costamount,
                               Orig_Trml_Id,
                               Dest_Trml_Id
                          FROM FS_RT_LEG
                         WHERE Rt_Plan_Id IN
                               (SELECT RP.RT_PLAN_ID
                                  FROM FS_RT_PLAN RP, QMS_QUOTE_MASTER QM
                                 WHERE RP.Quote_Id IN  QM.Quote_Id
                                      AND RP.ORIG_LOC_ID = QM.ORIGIN_LOCATION
                                       AND RP.DEST_LOC_ID = QM.DEST_LOCATION
                                       AND  QM.ID= J.ID)-- ADD ONE UNIQUE COLUMN IN ROUTE PALN (ID, MULTIQUOTE LANR NUMBER) TO HANDLE THE br, rsr SELECTED FOR SAME ORGING AND DESTINATION
                               AND Rt_Plan_Id = I.RT_PLAN_ID;

END LOOP;

              INSERT INTO QMS_QUOTE_RATES
                  (
                    QUOTE_ID,
                    SELL_BUY_FLAG,
                    BUYRATE_ID,
                    SELLRATE_ID,
                    RATE_LANE_NO,
                    CHARGE_ID,
                    CHARGE_DESCRIPTION,
                    MARGIN_DISCOUNT_FLAG,
                    MARGIN_TYPE,
                    MARGIN,
                    DISCOUNT_TYPE,
                    DISCOUNT,
                    NOTES,
                    QUOTE_REFNO,
                    BREAK_POINT,
                    CHARGE_AT,
                    BUY_RATE,
                    R_SELL_RATE,
                    RT_PLAN_ID,
                    SERIAL_NO,
                    ID,
                    ZONE_CODE,
                    LINE_NO,
                    MARGIN_TEST_FLAG,
                    SRVLEVEL,
                    FREQUENCY,
                    CARRIER,
                    TRANSIT_TIME,
                    RATE_VALIDITY,
                    FREQUENCY_CHECKED,
                    CARRIER_CHECKED,
                    TRANSIT_CHECKED,
                    VALIDITY_CHECKED,
                    VERSION_NO,
                    MULTI_QUOTE_LANE_NO,
                    EXTERNAL_NOTES
             )
             SELECT
                p_Newuniqueid1,
                SELL_BUY_FLAG,
                BUYRATE_ID,
                SELLRATE_ID,
                RATE_LANE_NO,
                CHARGE_ID,
                CHARGE_DESCRIPTION,
                MARGIN_DISCOUNT_FLAG,
                MARGIN_TYPE,
                MARGIN,
                DISCOUNT_TYPE,
                DISCOUNT,
                NOTES,
                QUOTE_REFNO,
                BREAK_POINT,
                CHARGE_AT,
                BUY_RATE,
                R_SELL_RATE,
                RT_PLAN_ID,
                SERIAL_NO,
                Seq_Qms_Quote_Rates.NEXTVAL,
                ZONE_CODE,
                LINE_NO,
                MARGIN_TEST_FLAG,
                SRVLEVEL,
                FREQUENCY,
                CARRIER,
                TRANSIT_TIME,
                RATE_VALIDITY,
                FREQUENCY_CHECKED,
                CARRIER_CHECKED,
                TRANSIT_CHECKED,
                VALIDITY_CHECKED,
                VERSION_NO,
                MULTI_QUOTE_LANE_NO,
                EXTERNAL_NOTES
          FROM QMS_QUOTE_RATES
          WHERE quote_id =  J.ID;

          UPDATE QMS_QUOTE_MASTER SET Active_Flag = 'I' WHERE Id = j.id;


              END LOOP;

         END IF;


    --KISHROE

  END;

  /*
     This procedure is called when the user clicks on a Quote ID from the updated quotes report to modify the updated quote.
     The updated rates/charges/cartages along with the other selected rates/charges/cartages are placed in a
     global temporary table QMS_QUOTE_RATES_TEMP and then qms_quote_pack.updated_quote_info_modify is called
     to read from the table so that existing code can be reused.

     The IN parameters are
         p_quoteid             NUMBER,
         p_sellbuyflag         VARCHAR2,
         p_changedesc          VARCHAR2

     The OUT Parameters are
        p_rs            OUT   resultset,
        p_rs1           OUT   resultset,
        p_rs2           OUT   resultset,
        p_rs3           OUT   resultset
  */
  PROCEDURE Qms_Updated_Modify_Quote(p_Quoteid     NUMBER,
                                     p_Sellbuyflag VARCHAR2,
                                     p_Changedesc  VARCHAR2,
                                     p_Rs          OUT Resultset,
                                     p_Rs1         OUT Resultset,
                                     p_Rs2         OUT Resultset,
                                     p_Rs3         OUT Resultset) AS
    v_Sql         VARCHAR2(1000) := '';
    v_Shmode      VARCHAR2(5);
    v_Weightbreak VARCHAR2(10);
    /*v_salesperson        VARCHAR2 (50);*/
    v_Servicelevel       VARCHAR2(15);
    v_Marginid           VARCHAR2(10);
    v_Marginforfreight   NUMBER(8, 2);
    v_Marginforcharges   NUMBER(8, 2);
    v_Marginforcartage   NUMBER(8, 2);
    v_Discountforfreight NUMBER(8, 2);
    v_Discountforcharges NUMBER(8, 2);
    v_Discountforcartage NUMBER(8, 2);
    --v_Quoteid            Number(16);
    v_Quoteid            VARCHAR2(100);-- added by subrahmanyam for 146970
    v_terminalid         VARCHAR2(15);
    v_flag                VARCHAR2(15);
    v_oldchargebasis      VARCHAR2(15); --@@Added by Kameswari for the WPBN issue-154398 on 19/02/09
    v_oldwtbreak      VARCHAR2(15);
    v_newchargebasis      VARCHAR2(15);
    v_newwtbreak      VARCHAR2(15);
    v_newchargeid      VARCHAR2(15);
    v_oldchargeid      VARCHAR2(15);--@@WPBN issue-154398
   --@@Added by subrahmanyam for the wpbnId: 195374 on 22-Jan-10
     v_Frequency_Checked VARCHAR2(15);
    v_Carrier_Checked VARCHAR2(15);
    v_Transit_Checked VARCHAR2(15);
    v_Validity_Checked VARCHAR2(15);
    V_charge_description  VARCHAR2(150);
    v_charge_at           VARCHAR2(15);
    v_serial_no         NUMBER(16);
    V_Margin_Discount_Flag VARCHAR2(1);
    V_Margin_Type          VARCHAR2(1);
    V_Margin               NUMBER(20,5);
    V_Discount_type        VARCHAR2(1);
    V_Discount             NUMBER(20,5);
    v_count               NUMBER(10);
    v_is_multi_quote      VARCHAR2(2);--Added on 04Feb2011
    --@@ Ended by subrahmanyam for the wpbnId: 195374 on 22-Jan-10
  BEGIN
    EXECUTE IMMEDIATE ('TRUNCATE TABLE QMS_QUOTE_RATES_TEMP');

    IF (UPPER(p_Sellbuyflag) = 'BR') THEN
      SELECT Rl.Shpmnt_Mode
        INTO v_Shmode
        FROM FS_RT_LEG Rl, FS_RT_PLAN Rp, QMS_QUOTES_UPDATED Qu
       WHERE Rl.Rt_Plan_Id = Rp.Rt_Plan_Id
         AND Rl.Serial_No =
             (SELECT DISTINCT Iqr.Serial_No
                FROM QMS_QUOTE_RATES Iqr
               WHERE Iqr.Quote_Id = Qu.Quote_Id
                 AND DECODE(Iqr.Sell_Buy_Flag,
                            'RSR',
                            DECODE(Iqr.Margin_Discount_Flag, 'M', 'BR'),
                            'CSR',
                            DECODE(Iqr.Margin_Discount_Flag, 'M', 'BR'),
                            Iqr.Sell_Buy_Flag) = Qu.Sell_Buy_Flag
                  AND Iqr.Buyrate_Id = Qu.Old_Buycharge_Id
                 AND Iqr.Rate_Lane_No = Qu.Old_Lane_No)
         AND Rp.Quote_Id =
             (SELECT Quote_Id FROM QMS_QUOTE_MASTER WHERE Id = Qu.Quote_Id)
         AND Qu.Quote_Id = p_Quoteid
         AND Qu.Changedesc = p_Changedesc;
      SELECT DISTINCT Bm.Weight_Break, Bd.Service_Level
        INTO v_Weightbreak, v_Servicelevel
        FROM QMS_BUYRATES_MASTER Bm,
             QMS_BUYRATES_DTL    Bd,
             QMS_QUOTES_UPDATED  Qu
       WHERE Bm.Buyrateid = Bd.Buyrateid
         AND Bd.Buyrateid = Qu.Old_Buycharge_Id
         AND Bd.Lane_No = Qu.Old_Lane_No
         AND Qu.Sell_Buy_Flag = p_Sellbuyflag
         AND Qu.Quote_Id = p_Quoteid
         AND Qu.Changedesc = p_Changedesc
         AND Bd.Line_No = '0';
    ELSIF (UPPER(p_Sellbuyflag) = 'RSR' OR UPPER(p_Sellbuyflag) = 'CSR') THEN
      SELECT Rl.Shpmnt_Mode
        INTO v_Shmode
        FROM FS_RT_LEG Rl, FS_RT_PLAN Rp, QMS_QUOTES_UPDATED Qu
       WHERE Rl.Rt_Plan_Id = Rp.Rt_Plan_Id
         AND Rl.Serial_No =
             (SELECT DISTINCT Iqr.Serial_No
                FROM QMS_QUOTE_RATES Iqr
               WHERE Iqr.Quote_Id = Qu.Quote_Id
                 AND Iqr.Sell_Buy_Flag = Qu.Sell_Buy_Flag
                AND Iqr.Sellrate_Id = Qu.Old_Sellcharge_Id
                 AND Iqr.Buyrate_Id = Qu.Old_Buycharge_Id
                 AND Iqr.Rate_Lane_No = Qu.Old_Lane_No)
         AND Rp.Quote_Id =
             (SELECT Quote_Id FROM QMS_QUOTE_MASTER WHERE Id = Qu.Quote_Id)
         AND Qu.Quote_Id = p_Quoteid
         AND Qu.Changedesc = p_Changedesc;
      SELECT DISTINCT Sm.Weight_Break, Sd.Servicelevel_Id
        INTO v_Weightbreak, v_Servicelevel
        FROM QMS_REC_CON_SELLRATESMASTER Sm,
             QMS_REC_CON_SELLRATESDTL    Sd,
             QMS_QUOTES_UPDATED          Qu
       WHERE Sm.Rec_Con_Id = Sd.Rec_Con_Id
         AND Sd.Rec_Con_Id = Qu.Old_Sellcharge_Id
         AND Sd.Buyrateid = Qu.Old_Buycharge_Id
         AND Sd.Lane_No = Qu.Old_Lane_No
         AND Sd.Servicelevel_Id NOT IN ('SCH')
         AND Sd.Line_No = '0'
         AND Qu.Sell_Buy_Flag = p_Sellbuyflag
         AND Qu.Quote_Id = p_Quoteid
         AND Qu.Changedesc = p_Changedesc;
    END IF;

    IF (UPPER(p_Sellbuyflag) = 'BR' OR UPPER(p_Sellbuyflag) = 'RSR' OR
       UPPER(p_Sellbuyflag) = 'CSR') THEN
      IF (v_Shmode = '1') THEN
        v_Marginid := '1';
      ELSIF (v_Shmode = '2' AND v_Weightbreak = 'LIST') THEN
        v_Marginid := '4';
      ELSIF (v_Shmode = '2' AND v_Weightbreak <> 'LIST') THEN
        v_Marginid := '2';
      ELSIF (v_Shmode = '4' AND v_Weightbreak = 'LIST') THEN
        v_Marginid := '15';
      ELSIF (v_Shmode = '4' AND v_Weightbreak <> 'LIST') THEN
        v_Marginid := '7';
      END IF;
      BEGIN
        SELECT Minmargins, Maxdiscount
          INTO v_Marginforfreight, v_Discountforfreight
          FROM QMS_MARGIN_LIMIT_DTL
         WHERE Invalidate = 'F'
           AND Margin_Id = v_Marginid
           AND Service_Level = v_Servicelevel
           AND Chargetype = 'FREIGHT'
           AND Levelno =
               (SELECT Level_No
                  FROM QMS_DESIGNATION
                 WHERE Designation_Id =
                       (SELECT Designation_Id
                          FROM FS_USERMASTER
                         WHERE Empid = (SELECT Sales_Person
                                          FROM QMS_QUOTE_MASTER
                                         WHERE Id = p_Quoteid)));
      EXCEPTION
        WHEN NO_DATA_FOUND THEN
          v_Marginforfreight   := 0;
          v_Discountforfreight := 0;
      END;
    ELSIF (UPPER(p_Sellbuyflag) = 'B' OR UPPER(p_Sellbuyflag) = 'S') THEN
      BEGIN
        SELECT Minmargins, Maxdiscount
          INTO v_Marginforcharges, v_Discountforcharges
          FROM QMS_MARGIN_LIMIT_DTL
         WHERE Invalidate = 'F'
           AND Chargetype = 'CHARGES'
           AND Levelno =
               (SELECT Level_No
                  FROM QMS_DESIGNATION
                 WHERE Designation_Id =
                       (SELECT Designation_Id
                          FROM FS_USERMASTER
                         WHERE Empid = (SELECT Sales_Person
                                          FROM QMS_QUOTE_MASTER
                                         WHERE Id = p_Quoteid)));
      EXCEPTION
        WHEN NO_DATA_FOUND THEN
          v_Marginforcharges   := 0;
          v_Discountforcharges := 0;
      END;
    ELSIF (UPPER(p_Sellbuyflag) = 'BC' OR UPPER(p_Sellbuyflag) = 'SC') THEN
      BEGIN
        SELECT Minmargins, Maxdiscount
          INTO v_Marginforcartage, v_Discountforcartage
          FROM QMS_MARGIN_LIMIT_DTL
         WHERE Invalidate = 'F'
           AND Chargetype = 'CARTAGES'
           AND Levelno =
               (SELECT Level_No
                  FROM QMS_DESIGNATION
                 WHERE Designation_Id =
                       (SELECT Designation_Id
                          FROM FS_USERMASTER
                         WHERE Empid = (SELECT Sales_Person
                                          FROM QMS_QUOTE_MASTER
                                         WHERE Id = p_Quoteid)));
      EXCEPTION
        WHEN NO_DATA_FOUND THEN
          v_Marginforcartage   := 0;
          v_Discountforcartage := 0;
      END;
    END IF;
    IF UPPER(p_Sellbuyflag) = 'BR' THEN
      v_Sql := ' and (qr.buyrate_id,decode(qr.sell_buy_flag,''RSR'',decode(qr.margin_discount_flag,''M'',''BR''),''CSR'',decode(qr.margin_discount_flag,''M'',''BR''),qr.sell_buy_flag),qr.rate_lane_no) not in (select old_buycharge_id,sell_buy_flag,old_lane_no from qms_quotes_updated where quote_id=' ||
               p_Quoteid || ' and changedesc=' || '''' || p_Changedesc || '''' || ')';
    ELSIF (UPPER(p_Sellbuyflag) = 'RSR' OR UPPER(p_Sellbuyflag) = 'CSR') THEN
      v_Sql := ' and (qr.sell_buy_flag,qr.sellrate_id,qr.buyrate_id,qr.rate_lane_no) not in (select sell_buy_flag,old_sellcharge_id,old_buycharge_id,old_lane_no from qms_quotes_updated where quote_id=' ||
               p_Quoteid || ' and changedesc=' || '''' || p_Changedesc || '''' || ')';
      --@@Commented and Modified by Kameswari for the WPBN issue-129343
    ELSIF UPPER(p_Sellbuyflag) = 'B' THEN
      /* v_Sql := ' and (decode(qr.SELL_BUY_FLAG,''S'',decode(qr.margin_discount_flag,''M'',''B''),qr.SELL_BUY_FLAG), qr.BUYRATE_ID) not in (select sell_buy_flag,old_buycharge_id from qms_quotes_updated where quote_id=' ||
                     p_Quoteid || ' and changedesc=' || '''' || p_Changedesc || '''' || ')';
      */
      v_Sql := ' and (decode(qr.SELL_BUY_FLAG,''S'',decode(qr.margin_discount_flag,''M'',''B''),qr.SELL_BUY_FLAG), qr.BUYRATE_ID,qr.charge_at) not in (select sell_buy_flag,old_buycharge_id,charge_at from qms_quotes_updated where quote_id=' ||
               p_Quoteid || ' and changedesc=' || '''' || p_Changedesc || '''' || ')';

      --@@Commented and Modified by Kameswari for the WPBN issue-129343
    ELSIF UPPER(p_Sellbuyflag) = 'S' THEN
      /*  v_Sql := ' and (qr.SELL_BUY_FLAG, qr.BUYRATE_ID, qr.SELLRATE_ID) not in (select sell_buy_flag, old_buycharge_id, old_sellcharge_id from qms_quotes_updated where quote_id=' ||
                     p_Quoteid || ' and changedesc=' || '''' || p_Changedesc || '''' || ')';
      */
      v_Sql := ' and (qr.SELL_BUY_FLAG, qr.BUYRATE_ID, qr.SELLRATE_ID,qr.charge_at) not in (select sell_buy_flag, old_buycharge_id, old_sellcharge_id,charge_at from qms_quotes_updated where quote_id=' ||
               p_Quoteid || ' and changedesc=' || '''' || p_Changedesc || '''' || ')';

    ELSIF UPPER(p_Sellbuyflag) = 'BC' THEN
      v_Sql := ' and qm.id=qr.quote_id and (qr.buyrate_id,decode(qr.charge_at,''Pickup'',qm.shipperzones,''Delivery'',qm.consigneezones),decode(qr.sell_buy_flag,''SC'',decode(qr.margin_discount_flag,''M'',''BC''),qr.sell_buy_flag)) not in (select old_buycharge_id,zone_code,sell_buy_flag from qms_quotes_updated where quote_id=' ||
               p_Quoteid || ' and changedesc=' || '''' || p_Changedesc || '''' || ')';
    ELSIF UPPER(p_Sellbuyflag) = 'SC' THEN
      v_Sql := ' and qm.id=qr.quote_id and (qr.buyrate_id,qr.SELLRATE_ID,decode(qr.charge_at,''Pickup'',qm.shipperzones,''Delivery'',qm.consigneezones),qr.sell_buy_flag) not in (select old_buycharge_id,old_sellcharge_id,zone_code,sell_buy_flag from qms_quotes_updated where quote_id=' ||
               p_Quoteid || ' and changedesc=' || '''' || p_Changedesc || '''' || ')';
    END IF;
    IF (UPPER(p_Sellbuyflag) = 'BC' OR UPPER(p_Sellbuyflag) = 'SC') THEN

      /*  Execute Immediate (' INSERT INTO qms_quote_rates_temp (QUOTE_ID,SELL_BUY_FLAG,BUYRATE_ID,SELLRATE_ID,RATE_LANE_NO,CHARGE_ID,CHARGE_DESCRIPTION ,MARGIN_DISCOUNT_FLAG,MARGIN_TYPE,MARGIN,DISCOUNT_TYPE,DISCOUNT ,NOTES ,QUOTE_REFNO,BREAK_POINT,CHARGE_AT,BUY_RATE,R_SELL_RATE,RT_PLAN_ID,SERIAL_NO,ZONE_CODE, line_no) ' ||
                              ' SELECT ' || p_Quoteid ||
                              ',qr.SELL_BUY_FLAG, qr.BUYRATE_ID, qr.SELLRATE_ID, qr.RATE_LANE_NO, qr.CHARGE_ID, qr.CHARGE_DESCRIPTION, qr.MARGIN_DISCOUNT_FLAG, qr.MARGIN_TYPE, qr.MARGIN, qr.DISCOUNT_TYPE, qr.DISCOUNT, qr.NOTES, qr.QUOTE_REFNO, qr.BREAK_POINT, qr.CHARGE_AT, qr.BUY_RATE, qr.R_SELL_RATE, qr.RT_PLAN_ID, qr.SERIAL_NO, qr.ZONE_CODE, qr.line_no ' ||
                              ' FROM qms_quote_rates qr, qms_quote_master qm WHERE qr.quote_id=' ||
                              p_Quoteid ||' and (qr.sell_buy_flag =''BR'' or qr.sell_buy_flag=''RSR'')'|| v_Sql
                               );
      */ --@@Commented and Modified by Kameswari for the WPBN issue-129343

      /*Execute Immediate (' INSERT INTO qms_quote_rates_temp (QUOTE_ID,SELL_BUY_FLAG,BUYRATE_ID,SELLRATE_ID,RATE_LANE_NO,CHARGE_ID,CHARGE_DESCRIPTION ,MARGIN_DISCOUNT_FLAG,MARGIN_TYPE,MARGIN,DISCOUNT_TYPE,DISCOUNT ,NOTES ,QUOTE_REFNO,BREAK_POINT,CHARGE_AT,BUY_RATE,R_SELL_RATE,RT_PLAN_ID,SERIAL_NO,ZONE_CODE, line_no) ' ||
                        ' SELECT ' || p_Quoteid ||
                        ',qr.SELL_BUY_FLAG, qr.BUYRATE_ID, qr.SELLRATE_ID, qr.RATE_LANE_NO, qr.CHARGE_ID, qr.CHARGE_DESCRIPTION, qr.MARGIN_DISCOUNT_FLAG, qr.MARGIN_TYPE, qr.MARGIN, qr.DISCOUNT_TYPE, qr.DISCOUNT, qr.NOTES, qr.QUOTE_REFNO, qr.BREAK_POINT, qr.CHARGE_AT, qr.BUY_RATE, qr.R_SELL_RATE, qr.RT_PLAN_ID, qr.SERIAL_NO, qr.ZONE_CODE, qr.line_no ' ||
                        ' FROM qms_quote_rates qr, qms_quote_master qm WHERE qr.quote_id=' ||
                        p_Quoteid ||
                        ' and (qr.sell_buy_flag =''B'' or qr.sell_buy_flag=''S'') and qr.Charge_Description  in(select chargedescid from qms_charge_groupsmaster where chargegroup_id in (
                   Select chargegroupid from qms_quote_chargegroupdtl where quote_id= ' ||
                        p_Quoteid || ') and inactivate=''N'')' || v_Sql ||
                        ' Union SELECT ' || p_Quoteid ||
                        ',qr.SELL_BUY_FLAG, qr.BUYRATE_ID, qr.SELLRATE_ID, qr.RATE_LANE_NO, qr.CHARGE_ID, qr.CHARGE_DESCRIPTION, qr.MARGIN_DISCOUNT_FLAG, qr.MARGIN_TYPE, qr.MARGIN, qr.DISCOUNT_TYPE, qr.DISCOUNT, qr.NOTES, qr.QUOTE_REFNO, qr.BREAK_POINT, qr.CHARGE_AT, qr.BUY_RATE, qr.R_SELL_RATE, qr.RT_PLAN_ID, qr.SERIAL_NO, qr.ZONE_CODE, qr.line_no ' ||
                        ' FROM qms_quote_rates qr, qms_quote_master qm WHERE qr.quote_id=' ||
                        p_Quoteid ||
                                          'and (qr.sell_buy_flag =''BR'' or qr.sell_buy_flag=''RSR'' or qr.sell_buy_flag=''CSR'' or qr.sell_buy_flag=''SBR'' or qr.sell_buy_flag=''BC'' or qr.sell_buy_flag=''SC'' )' ||
                        v_Sql);*/--@@Modified for the WPBN issues-146448,146968 on 19/12/08


        /*'and (qr.sell_buy_flag =''BR'' or qr.sell_buy_flag=''RSR'' or qr.sell_buy_flag=''BC'' or qr.sell_buy_flag=''SC'' )' ||
                                                                                                                                                                  v_Sql);*/ --@@Modified by Kameswari for the WPBN issue-13237

  EXECUTE IMMEDIATE (' INSERT INTO qms_quote_rates_temp (QUOTE_ID,SELL_BUY_FLAG,BUYRATE_ID,SELLRATE_ID,RATE_LANE_NO,VERSION_NO,CHARGE_ID,CHARGE_DESCRIPTION ,MARGIN_DISCOUNT_FLAG,MARGIN_TYPE,MARGIN,DISCOUNT_TYPE,DISCOUNT ,NOTES ,QUOTE_REFNO,BREAK_POINT,CHARGE_AT,BUY_RATE,R_SELL_RATE,RT_PLAN_ID,SERIAL_NO,ZONE_CODE, line_no) ' ||
                        ' SELECT ' || p_Quoteid ||
                        ',qr.SELL_BUY_FLAG, qr.BUYRATE_ID, qr.SELLRATE_ID, qr.RATE_LANE_NO, qr.VERSION_NO,qr.CHARGE_ID, qr.CHARGE_DESCRIPTION, qr.MARGIN_DISCOUNT_FLAG, qr.MARGIN_TYPE, qr.MARGIN, qr.DISCOUNT_TYPE, qr.DISCOUNT, qr.NOTES, qr.QUOTE_REFNO, qr.BREAK_POINT, qr.CHARGE_AT, qr.BUY_RATE, qr.R_SELL_RATE, qr.RT_PLAN_ID, qr.SERIAL_NO, qr.ZONE_CODE, qr.line_no ' ||
                        ' FROM qms_quote_rates qr, qms_quote_master qm WHERE qr.quote_id=' ||
                        p_Quoteid ||
                        ' and (qr.sell_buy_flag =''B'' or qr.sell_buy_flag=''S'') and qr.Charge_Description  in(select chargedescid from qms_charge_groupsmaster where chargegroup_id in (
                   SELECT chargegroupid FROM QMS_QUOTE_CHARGEGROUPDTL WHERE quote_id= ' ||
                        p_Quoteid || ') and inactivate=''N'')' || v_Sql ||
                        ' Union SELECT ' || p_Quoteid ||
                        ',qr.SELL_BUY_FLAG, qr.BUYRATE_ID, qr.SELLRATE_ID, qr.RATE_LANE_NO, qr.VERSION_NO,qr.CHARGE_ID, qr.CHARGE_DESCRIPTION, qr.MARGIN_DISCOUNT_FLAG, qr.MARGIN_TYPE, qr.MARGIN, qr.DISCOUNT_TYPE, qr.DISCOUNT, qr.NOTES, qr.QUOTE_REFNO, qr.BREAK_POINT, qr.CHARGE_AT, qr.BUY_RATE, qr.R_SELL_RATE, qr.RT_PLAN_ID, qr.SERIAL_NO, qr.ZONE_CODE, qr.line_no ' ||
                        ' FROM qms_quote_rates qr, qms_quote_master qm WHERE qr.quote_id=' ||
                        p_Quoteid ||
                         'and (qr.sell_buy_flag =''BR'' or qr.sell_buy_flag=''RSR'' or qr.sell_buy_flag=''CSR'' or qr.sell_buy_flag=''SBR'' or qr.sell_buy_flag=''BC'' or qr.sell_buy_flag=''SC'' )' ||
                        v_Sql);
                          /*'and (qr.sell_buy_flag =''BR'' or qr.sell_buy_flag=''RSR'' or qr.sell_buy_flag=''BC'' or qr.sell_buy_flag=''SC'' )' ||
                                                                                                                                                                  v_Sql);*/ --@@Modified by Kameswari for the WPBN issue-13237


    ELSIF (UPPER(p_Sellbuyflag) = 'BR' OR UPPER(p_Sellbuyflag) = 'RSR') THEN
      EXECUTE IMMEDIATE (' INSERT INTO qms_quote_rates_temp (QUOTE_ID,SELL_BUY_FLAG,BUYRATE_ID,SELLRATE_ID,RATE_LANE_NO,CHARGE_ID,CHARGE_DESCRIPTION ,MARGIN_DISCOUNT_FLAG,MARGIN_TYPE,MARGIN,DISCOUNT_TYPE,DISCOUNT ,NOTES ,QUOTE_REFNO,BREAK_POINT,CHARGE_AT,BUY_RATE,R_SELL_RATE,RT_PLAN_ID,SERIAL_NO,ZONE_CODE, line_no) ' ||
                        ' SELECT ' || p_Quoteid ||
                        ',qr.SELL_BUY_FLAG, qr.BUYRATE_ID, qr.SELLRATE_ID, qr.RATE_LANE_NO, qr.CHARGE_ID, qr.CHARGE_DESCRIPTION, qr.MARGIN_DISCOUNT_FLAG, qr.MARGIN_TYPE, qr.MARGIN, qr.DISCOUNT_TYPE, qr.DISCOUNT, qr.NOTES, qr.QUOTE_REFNO, qr.BREAK_POINT, qr.CHARGE_AT, qr.BUY_RATE, qr.R_SELL_RATE, qr.RT_PLAN_ID, qr.SERIAL_NO, qr.ZONE_CODE, qr.line_no ' ||
                        ' FROM qms_quote_rates qr WHERE qr.quote_id=' ||
                        p_Quoteid ||
                        '  and (qr.sell_buy_flag =''B'' or qr.sell_buy_flag=''S'') and qr.Charge_Description  in(select chargedescid from qms_charge_groupsmaster where chargegroup_id in (
                   SELECT chargegroupid FROM QMS_QUOTE_CHARGEGROUPDTL WHERE quote_id= ' ||
                        p_Quoteid || ') and inactivate=''N'')' || v_Sql ||
                        'Union SELECT ' || p_Quoteid ||
                        ',qr.SELL_BUY_FLAG, qr.BUYRATE_ID, qr.SELLRATE_ID, qr.RATE_LANE_NO, qr.CHARGE_ID, qr.CHARGE_DESCRIPTION, qr.MARGIN_DISCOUNT_FLAG, qr.MARGIN_TYPE, qr.MARGIN, qr.DISCOUNT_TYPE, qr.DISCOUNT, qr.NOTES, qr.QUOTE_REFNO, qr.BREAK_POINT, qr.CHARGE_AT, qr.BUY_RATE, qr.R_SELL_RATE, qr.RT_PLAN_ID, qr.SERIAL_NO, qr.ZONE_CODE, qr.line_no ' ||
                        ' FROM qms_quote_rates qr WHERE qr.quote_id=' ||
                        p_Quoteid ||
                        ' and (qr.sell_buy_flag =''BC'' or qr.sell_buy_flag =''BR'' or qr.sell_buy_flag =''SBR'' or qr.sell_buy_flag =''CSR'' or qr.sell_buy_flag =''RSR'' or qr.sell_buy_flag=''SC'')' ||
                        v_Sql);--qr.sell_buy_flag =''BR'' is included for issue 166677
    ELSE

      /*Execute Immediate (' INSERT INTO qms_quote_rates_temp (QUOTE_ID,SELL_BUY_FLAG,BUYRATE_ID,SELLRATE_ID,RATE_LANE_NO,CHARGE_ID,CHARGE_DESCRIPTION ,MARGIN_DISCOUNT_FLAG,MARGIN_TYPE,MARGIN,DISCOUNT_TYPE,DISCOUNT ,NOTES ,QUOTE_REFNO,BREAK_POINT,CHARGE_AT,BUY_RATE,R_SELL_RATE,RT_PLAN_ID,SERIAL_NO,ZONE_CODE, line_no) ' ||
                        ' SELECT ' || p_Quoteid ||
                        ',qr.SELL_BUY_FLAG, qr.BUYRATE_ID, qr.SELLRATE_ID, qr.RATE_LANE_NO, qr.CHARGE_ID, qr.CHARGE_DESCRIPTION, qr.MARGIN_DISCOUNT_FLAG, qr.MARGIN_TYPE, qr.MARGIN, qr.DISCOUNT_TYPE, qr.DISCOUNT, qr.NOTES, qr.QUOTE_REFNO, qr.BREAK_POINT, qr.CHARGE_AT, qr.BUY_RATE, qr.R_SELL_RATE, qr.RT_PLAN_ID, qr.SERIAL_NO, qr.ZONE_CODE, qr.line_no ' ||
                        ' FROM qms_quote_rates qr WHERE qr.quote_id=' ||
                        p_Quoteid ||
                        ' and (qr.sell_buy_flag =''B'' or qr.sell_buy_flag=''S'') and qr.Charge_Description  in(select chargedescid from qms_charge_groupsmaster where chargegroup_id in (
                   Select chargegroupid from qms_quote_chargegroupdtl where quote_id= ' ||
                        p_Quoteid || ') and inactivate=''N'')' || v_Sql ||
                        ' Union SELECT ' || p_Quoteid ||
                        ',qr.SELL_BUY_FLAG, qr.BUYRATE_ID, qr.SELLRATE_ID, qr.RATE_LANE_NO, qr.CHARGE_ID, qr.CHARGE_DESCRIPTION, qr.MARGIN_DISCOUNT_FLAG, qr.MARGIN_TYPE, qr.MARGIN, qr.DISCOUNT_TYPE, qr.DISCOUNT, qr.NOTES, qr.QUOTE_REFNO, qr.BREAK_POINT, qr.CHARGE_AT, qr.BUY_RATE, qr.R_SELL_RATE, qr.RT_PLAN_ID, qr.SERIAL_NO, qr.ZONE_CODE, qr.line_no ' ||
                        ' FROM qms_quote_rates qr WHERE qr.quote_id=' ||
                        p_Quoteid ||
                        'and qr.sell_buy_flag in (''BR'',''RSR'',''SBR'',''CSR'',''BC'',''SC'')' ||
                        v_sql);*/--@@Modified for the WPBN issues-146448,146968 on 19/12/08



    EXECUTE IMMEDIATE (' INSERT INTO qms_quote_rates_temp (QUOTE_ID,SELL_BUY_FLAG,BUYRATE_ID,SELLRATE_ID,RATE_LANE_NO,VERSION_NO,CHARGE_ID,CHARGE_DESCRIPTION ,MARGIN_DISCOUNT_FLAG,MARGIN_TYPE,MARGIN,DISCOUNT_TYPE,DISCOUNT ,NOTES ,QUOTE_REFNO,BREAK_POINT,CHARGE_AT,BUY_RATE,R_SELL_RATE,RT_PLAN_ID,SERIAL_NO,ZONE_CODE, line_no) ' ||
                        ' SELECT ' || p_Quoteid ||
                        ',qr.SELL_BUY_FLAG, qr.BUYRATE_ID, qr.SELLRATE_ID, qr.RATE_LANE_NO, qr.VERSION_NO,qr.CHARGE_ID, qr.CHARGE_DESCRIPTION, qr.MARGIN_DISCOUNT_FLAG, qr.MARGIN_TYPE, qr.MARGIN, qr.DISCOUNT_TYPE, qr.DISCOUNT, qr.NOTES, qr.QUOTE_REFNO, qr.BREAK_POINT, qr.CHARGE_AT, qr.BUY_RATE, qr.R_SELL_RATE, qr.RT_PLAN_ID, qr.SERIAL_NO, qr.ZONE_CODE, qr.line_no ' ||
                        ' FROM qms_quote_rates qr WHERE qr.quote_id=' ||
                        p_Quoteid ||
                        ' and (qr.sell_buy_flag =''B'' or qr.sell_buy_flag=''S'') and qr.Charge_Description  in(select chargedescid from qms_charge_groupsmaster where chargegroup_id in (
                   SELECT chargegroupid FROM QMS_QUOTE_CHARGEGROUPDTL WHERE quote_id= ' ||
                        p_Quoteid || ') and inactivate=''N'')' || v_Sql ||
                        ' Union SELECT ' || p_Quoteid ||
                        ',qr.SELL_BUY_FLAG, qr.BUYRATE_ID, qr.SELLRATE_ID, qr.RATE_LANE_NO, qr.VERSION_NO,qr.CHARGE_ID, qr.CHARGE_DESCRIPTION, qr.MARGIN_DISCOUNT_FLAG, qr.MARGIN_TYPE, qr.MARGIN, qr.DISCOUNT_TYPE, qr.DISCOUNT, qr.NOTES, qr.QUOTE_REFNO, qr.BREAK_POINT, qr.CHARGE_AT, qr.BUY_RATE, qr.R_SELL_RATE, qr.RT_PLAN_ID, qr.SERIAL_NO, qr.ZONE_CODE, qr.line_no ' ||
                        ' FROM qms_quote_rates qr WHERE qr.quote_id=' ||
                        p_Quoteid ||
                        'and qr.sell_buy_flag in (''BR'',''RSR'',''SBR'',''CSR'',''BC'',''SC'')' ||
                        v_sql);
    END IF; --@@Added  by Kameswari for the WPBN issue-129343
-- BUYRATE_ID,RATE_LANE_NO is included for issue #166677

    IF UPPER(p_Sellbuyflag) = 'BR' OR UPPER(p_Sellbuyflag) = 'RSR' OR UPPER(p_Sellbuyflag) = 'CSR' THEN
     v_count :=0;
     FOR i IN (SELECT BUYRATE_ID,RATE_LANE_NO,break_point,Frequency_Checked,Carrier_Checked,Transit_Checked,Validity_Checked,Margin_Discount_Flag,Margin,Margin_type,Discount,Discount_type,Serial_No,Charge_Description,Charge_at FROM QMS_QUOTE_RATES WHERE quote_id=p_Quoteid AND sell_buy_flag IN ('BR','RSR','CSR'))
      LOOP
       --@@Added by subrahmanyam for 195374 on 22-Jan-10
      v_Frequency_Checked := i.frequency_checked;
      v_Carrier_Checked := i.carrier_checked;
      v_Transit_Checked := i.transit_checked;
      v_Validity_Checked := i.validity_checked;
      V_charge_description  := i.charge_description;
      v_charge_at           := i.charge_at;
      v_serial_no           := i.serial_no;
          --@@Ended by subrahmanyam for 195374 on 22-Jan-10
      INSERT INTO QMS_QUOTE_RATES_TEMP
        (Quote_Id,
         Sell_Buy_Flag,
         Sellrate_id,
         Buyrate_Id,
         Rate_Lane_No,
         Version_No,  --@@Added for the WPBN issues-146448,146968 on 19/12/08
         Charge_Description,
         Margin_Discount_Flag,
         Margin_Type,
         Margin,
         Discount_type,
         Discount,
         Break_Point,
         Line_No,
         Buy_Rate,
         R_SELL_RATE,
         Charge_At,
         Serial_No,
         Rate_Description,
         Frequency,
         Transit_Time,
         Carrier,
         rate_validity,
         Frequency_Checked,
         Carrier_Checked,
         Transit_Checked,
         Validity_Checked)
        SELECT DISTINCT p_Quoteid,
               Qu.Sell_Buy_Flag,
               Qu.New_Sellcharge_Id,
               Qu.New_Buycharge_Id,
               Qu.New_Lane_No,
               Qu.New_Version_No, --@@Added for the WPBN issues-146448,146968 on 19/12/08
               /*(Select Distinct Iqr.Charge_Description
                  From Qms_Quote_Rates Iqr
                 Where Iqr.Quote_Id = Qu.Quote_Id
                   And Decode(Iqr.Sell_Buy_Flag,
                              'RSR',
                              Decode(Iqr.Margin_Discount_Flag, 'M', 'BR'),
                              'CSR',
                              Decode(Iqr.Margin_Discount_Flag, 'M', 'BR'),
                              Iqr.Sell_Buy_Flag) = Qu.Sell_Buy_Flag
                    And Iqr.Buyrate_Id = Qu.Old_Buycharge_Id
                   And Iqr.Rate_Lane_No = Qu.Old_Lane_No) Charge_Desc,*/
                 i.charge_description,
              /* (Select Distinct Iqr.Margin_Discount_Flag
                  From Qms_Quote_Rates Iqr
                 Where Iqr.Quote_Id = Qu.Quote_Id
                   And Decode(Iqr.Sell_Buy_Flag,
                              'RSR',
                              Decode(Iqr.Margin_Discount_Flag, 'M', 'BR'),
                              'CSR',
                              Decode(Iqr.Margin_Discount_Flag, 'M', 'BR'),
                              Iqr.Sell_Buy_Flag) = Qu.Sell_Buy_Flag
                   And  Iqr.Sell_Buy_Flag = Qu.Sell_Buy_Flag           --@@Modified by Kameswari for the WPBN issue-140462
                   And Iqr.Buyrate_Id = Qu.Old_Buycharge_Id
                   And Iqr.Rate_Lane_No = Qu.Old_Lane_No) Margin_Discount_Flag,*/
               i.margin_discount_flag,
               i.margin_type,
               i.margin,
               i.discount_type,
               i.discount,
               i.break_point,
               Bd.Line_No,
               Bd.Chargerate,
                (SELECT chargerate FROM QMS_REC_CON_SELLRATESDTL WHERE buyrateid=Bd.Buyrateid AND lane_no=Bd.Lane_No AND weightbreakslab=Bd.Weight_Break_Slab AND version_no=Bd.Version_No AND  rec_con_id = qu.new_sellcharge_id AND ai_flag='A'),
               /*(Select Distinct Iqr.Charge_At
                  From Qms_Quote_Rates Iqr
                 Where Iqr.Quote_Id = Qu.Quote_Id
                   And Decode(Iqr.Sell_Buy_Flag,
                              'RSR',
                              Decode(Iqr.Margin_Discount_Flag, 'M', 'BR'),
                              'CSR',
                              Decode(Iqr.Margin_Discount_Flag, 'M', 'BR'),
                              Iqr.Sell_Buy_Flag) = Qu.Sell_Buy_Flag
                   And Iqr.Buyrate_Id = Qu.Old_Buycharge_Id
                   And Iqr.Rate_Lane_No = Qu.Old_Lane_No) Charge_At,*/
                   i.charge_at,
               /*(Select Distinct Iqr.Serial_No
                  From Qms_Quote_Rates Iqr
                 Where Iqr.Quote_Id = Qu.Quote_Id
                   And Decode(Iqr.Sell_Buy_Flag,
                              'RSR',
                              Decode(Iqr.Margin_Discount_Flag, 'M', 'BR'),
                              'CSR',
                              Decode(Iqr.Margin_Discount_Flag, 'M', 'BR'),
                              Iqr.Sell_Buy_Flag) = Qu.Sell_Buy_Flag
                   And Iqr.Buyrate_Id = Qu.Old_Buycharge_Id
                   And Iqr.Rate_Lane_No = Qu.Old_Lane_No) Serial_No,*/
               i.serial_no,
               Bd.Rate_Description,
               Bd.Frequency,
               Bd.Carrier_Id,
               Bd.Transit_Time,
               Bd.Valid_Upto,
              i.frequency_checked,
              i.carrier_checked,
              i.transit_checked,
              i.validity_checked
          FROM QMS_QUOTES_UPDATED Qu, QMS_BUYRATES_DTL Bd
         WHERE Qu.New_Buycharge_Id = Bd.Buyrateid
           AND Bd.Weight_Break_Slab=i.break_point
           AND Qu.New_Lane_No = Bd.Lane_No
           AND Qu.New_version_no=Bd.version_no
           AND Qu.New_version_no=(SELECT new_version_no FROM QMS_QUOTES_UPDATED WHERE quote_id=p_Quoteid AND changedesc=p_Changedesc)
           AND Qu.Quote_Id = p_Quoteid
           AND Qu.Changedesc = p_Changedesc
           AND qu.old_buycharge_id=i.buyrate_id
           AND qu.old_lane_no=i.rate_lane_no
           AND (UPPER(bd.rate_description)='A FREIGHT RATE' OR UPPER(bd.rate_description) = 'FREIGHT RATE')--kishore for quote view(update) duplicates breaks
            ORDER BY Bd.Line_No;
            v_count := v_count+1;

         END LOOP;


             --@@Added by subrahmanyam for 195374 on 22-Jan-10
           IF UPPER(p_Sellbuyflag) = 'BR' THEN
                 V_Margin_Discount_Flag := 'M';
                 V_Margin_Type          := 'P';
                 V_Margin               := '0.00000';
                 V_Discount_type        := '';
                 V_Discount             := '0.00000';
           ELSIF UPPER(p_Sellbuyflag) = 'RSR' THEN
                 V_Margin_Discount_Flag := 'D';
                 V_Margin_Type          := '';
                 V_Margin               := '0.00000';
                 V_Discount_type        := 'P';
                 V_Discount             := '0.00000';

           END IF;
           IF (v_Shmode = '2' AND v_Weightbreak = 'LIST') THEN

           FOR j IN ( SELECT QR.BREAK_POINT,QR.BUYRATE_ID,QR.RATE_LANE_NO,QR.VERSION_NO FROM QMS_QUOTE_RATES_TEMP QR ,QMS_QUOTES_UPDATED QU WHERE  QR.QUOTE_ID=p_Quoteid AND QR.BUYRATE_ID= QU.NEW_BUYCHARGE_ID
           	   AND QR.RATE_LANE_NO=QU.NEW_LANE_NO AND QR.VERSION_NO=NEW_VERSION_NO AND QU.QUOTE_ID=P_QUOTEID AND QU.CHANGEDESC=P_CHANGEDESC)
             LOOP
           BEGIN
            FOR K IN (SELECT DISTINCT BD.WEIGHT_BREAK_SLAB,BD.CHARGERATE,BD.FREQUENCY,BD.TRANSIT_TIME,BD.CARRIER_ID,BD.VALID_UPTO  FROM QMS_BUYRATES_DTL BD WHERE
              BD.BUYRATEID=J.BUYRATE_ID
              AND  BD.LANE_NO=J.RATE_LANE_NO
              AND BD.VERSION_NO = J.VERSION_NO
              AND NOT EXISTS(SELECT 'X' FROM
              QMS_QUOTE_RATES_TEMP QR WHERE  QR.QUOTE_ID=p_Quoteid AND QR.BUYRATE_ID=BD.BUYRATEID
              AND QR.RATE_LANE_NO=BD.LANE_NO AND QR.VERSION_NO=BD.VERSION_NO AND QR.BREAK_POINT=BD.WEIGHT_BREAK_SLAB )
              AND INSTR(BD.WEIGHT_BREAK_SLAB , j.BREAK_POINT,1)>0)
            LOOP

             INSERT INTO QMS_QUOTE_RATES_TEMP
        (Quote_Id,
         Sell_Buy_Flag,
         Sellrate_Id,
         Buyrate_Id,
         Rate_Lane_No,
         Version_No,
         Charge_Description,
         Margin_Discount_Flag,
         Margin_Type,
         Margin,
         Discount_type,
         Discount,
         Break_Point,
         Line_No,
         Buy_Rate,
         R_SELL_RATE,
         Charge_At,
         Serial_No,
         Id,
         Frequency,
         Carrier,
         Transit_Time,
         Rate_Validity,
         Frequency_checked,
         Carrier_Checked,
         Transit_checked,
         Validity_checked
      )
        SELECT p_Quoteid,
               Qu.Sell_Buy_Flag,
               Qu.New_Sellcharge_Id,
               Qu.New_Buycharge_Id,
               Qu.New_Lane_No,
               Qu.New_Version_No,
               V_charge_description,
               V_Margin_Discount_Flag,
               V_Margin_Type,
               V_Margin,
               V_Discount_type,
               V_Discount,
               K.WEIGHT_BREAK_SLAB,
              v_count,
               K.Chargerate,
               (SELECT chargerate FROM QMS_REC_CON_SELLRATESDTL WHERE buyrateid=Bd.Buyrateid AND lane_no=Bd.Lane_No AND weightbreakslab=Bd.Weight_Break_Slab AND version_no=Bd.Version_No AND ai_flag='A'),
               v_charge_at,
               v_serial_no,
               Seq_Qms_Quote_Rates.NEXTVAL,
               K.FREQUENCY,
               K.Carrier_Id,
               K.Transit_Time,
               K.Valid_Upto,
               V_frequency_checked,
               V_carrier_checked,
               V_transit_checked,
               v_Validity_Checked
          FROM QMS_QUOTES_UPDATED Qu, QMS_BUYRATES_DTL Bd
         WHERE Qu.New_Buycharge_Id = Bd.Buyrateid

           AND Qu.New_Lane_No = Bd.Lane_No
           AND Qu.New_version_no=Bd.version_no
           AND Qu.New_version_no=(SELECT new_version_no FROM QMS_QUOTES_UPDATED WHERE quote_id=p_Quoteid AND changedesc=p_Changedesc)
           AND Bd.Weight_Break_Slab=K.Weight_Break_Slab
           AND Qu.Quote_Id = p_Quoteid
           AND Qu.Changedesc = p_Changedesc
           AND  Qu.Old_Buycharge_Id=bd.buyrateid
           AND   Qu.Old_Lane_No=bd.lane_no;
         v_count := v_count+1;
            END LOOP;
            EXCEPTION WHEN NO_DATA_FOUND
           THEN
            DBMS_OUTPUT.put_line('NO EXTRA CHARGES FOUND..');
          END;
             END LOOP;
           ELSE
           BEGIN
           	  FOR J IN ( SELECT DISTINCT BD.WEIGHT_BREAK_SLAB,BD.CHARGERATE,BD.BUYRATEID,BD.LANE_NO,BD.VERSION_NO,BD.FREQUENCY,BD.CARRIER_ID,BD.TRANSIT_TIME,BD.VALID_UPTO FROM QMS_QUOTES_UPDATED QU,QMS_BUYRATES_DTL BD WHERE QUOTE_ID=P_QUOTEID AND CHANGEDESC=P_CHANGEDESC
                        AND QU.NEW_BUYCHARGE_ID = BD.BUYRATEID
                        AND QU.NEW_LANE_NO = BD.LANE_NO
                        AND QU.NEW_VERSION_NO=BD.VERSION_NO AND NOT EXISTS(SELECT 'X' FROM
                        QMS_QUOTE_RATES_TEMP QR WHERE  QR.QUOTE_ID=p_Quoteid AND QR.BUYRATE_ID=BD.BUYRATEID
                        AND QR.RATE_LANE_NO=BD.LANE_NO AND QR.VERSION_NO=BD.VERSION_NO AND QR.BREAK_POINT=BD.WEIGHT_BREAK_SLAB))
	           LOOP
             		  INSERT INTO QMS_QUOTE_RATES_TEMP
        (Quote_Id,
         Sell_Buy_Flag,
         Sellrate_Id,
         Buyrate_Id,
         Rate_Lane_No,
         Version_No,
         Charge_Description,
         Margin_Discount_Flag,
         Margin_Type,
         Margin,
         Discount_type,
         Discount,
         Break_Point,
         Line_No,
         Buy_Rate,
         R_SELL_RATE,
         Charge_At,
         Serial_No,
         Id,
         Frequency,
         Carrier,
         Transit_Time,
         Rate_Validity,
         Frequency_checked,
         Carrier_Checked,
         Transit_checked,
         Validity_checked
      )
        SELECT p_Quoteid,
               Qu.Sell_Buy_Flag,
               Qu.New_Sellcharge_Id,
               Qu.New_Buycharge_Id,
               Qu.New_Lane_No,
               Qu.New_Version_No,
               V_charge_description,
               V_Margin_Discount_Flag,
               V_Margin_Type,
               V_Margin,
               V_Discount_type,
               V_Discount,
               J.WEIGHT_BREAK_SLAB,
              v_count,
               J.Chargerate,
               (SELECT chargerate FROM QMS_REC_CON_SELLRATESDTL WHERE buyrateid=Bd.Buyrateid AND lane_no=Bd.Lane_No AND weightbreakslab=Bd.Weight_Break_Slab AND version_no=Bd.Version_No AND rec_con_id = Qu.New_Sellcharge_Id  AND ai_flag='A'),
               v_charge_at,
               v_serial_no,
               Seq_Qms_Quote_Rates.NEXTVAL,
               J.FREQUENCY,
               J.Carrier_Id,
               J.Transit_Time,
               J.Valid_Upto,
               V_frequency_checked,
               V_carrier_checked,
               V_transit_checked,
               v_Validity_Checked
          FROM QMS_QUOTES_UPDATED Qu, QMS_BUYRATES_DTL Bd
         WHERE Qu.New_Buycharge_Id = Bd.Buyrateid

           AND Qu.New_Lane_No = Bd.Lane_No
           AND Qu.New_version_no=Bd.version_no
           AND Qu.New_version_no=(SELECT new_version_no FROM QMS_QUOTES_UPDATED WHERE quote_id=p_Quoteid AND changedesc=p_Changedesc)
           AND Bd.Weight_Break_Slab=J.Weight_Break_Slab
           AND Qu.Quote_Id = p_Quoteid
           AND Qu.Changedesc = p_Changedesc
           AND  Qu.Old_Buycharge_Id=bd.buyrateid
           AND   Qu.Old_Lane_No=bd.lane_no;
         v_count := v_count+1;
             END LOOP;
            EXCEPTION WHEN NO_DATA_FOUND THEN
            DBMS_OUTPUT.put_line('No Extra charages are there for not list..');
            END;

           END IF;
      --@@Ended by subrahmanyam for 195374 on 22-Jan-10
     /* For i In (Select Qr.Break_Point,
                       Qr.Margin_Type,
                       Qr.Margin,
                       Qu.New_Buycharge_Id,
                       Qu.New_Lane_No,
                       Qu.New_Version_No,--@@Added by kameswari for the WPBN issue-146448 on 24/12/08
                       Qr.Frequency_Checked,
                       Qr.Carrier_Checked,
                       Qr.Transit_Checked,
                       Qr.Validity_Checked--@@ WPBN issue-146448 on 24/12/08

                  From Qms_Quote_Rates Qr, Qms_Quotes_Updated Qu
                 Where Decode(Qr.Sell_Buy_Flag,
                              'RSR',
                              Decode(Qr.Margin_Discount_Flag, 'M', 'BR'),
                              'CSR',
                              Decode(Qr.Margin_Discount_Flag, 'M', 'BR'),
                              Qr.Sell_Buy_Flag) = Qu.Sell_Buy_Flag
                   And Qr.Buyrate_Id = Qu.Old_Buycharge_Id
                   And Qr.Rate_Lane_No = Qu.Old_Lane_No
                   And Qu.Quote_Id = Qr.Quote_Id
                   And Qu.Quote_Id = p_Quoteid
                   And Qu.Changedesc = p_Changedesc) Loop
           Update Qms_Quote_Rates_Temp
           Set Margin_Type = i.Margin_Type, Margin = i.Margin,
           FREQUENCY_CHECKED=i.frequency_checked,CARRIER_CHECKED=i.carrier_checked,
           TRANSIT_CHECKED=i.transit_checked,VALIDITY_CHECKED=i.validity_checked
           Where Quote_Id = p_Quoteid
           And Break_Point = i.Break_Point
           And Sell_Buy_Flag = p_Sellbuyflag
           And Buyrate_Id = i.New_Buycharge_Id
           And Rate_Lane_No = i.New_Lane_No
           And Version_no=i.new_version_no  -@@Added by kameswari for the WPBN issue-146448 on 24/12/08
      End Loop;*/
   /* Elsif  Upper(p_Sellbuyflag) = 'SBR'
    Then
      Insert Into Qms_Quote_Rates_Temp
        (Quote_Id,
         Sell_Buy_Flag,
         Sellrate_Id,
         Buyrate_Id,
         Rate_Lane_No,
         Version_no,
         Charge_Description,
         Margin_Discount_Flag,
         Margin_Type,
         Margin,
         Discount_Type,
         Discount,
         Break_Point,
         Line_No,
         Buy_Rate,
         Charge_At,
         Serial_No,
         Rate_Description,
          Frequency,
         Transit_Time,
         Carrier,
        rate_validity,
        Frequency_Checked,
        Carrier_Checked,
        Transit_Checked,
        Validity_Checked
         )
        Select distinct p_Quoteid,
               Qu.Sell_Buy_Flag,
               Qu.New_Sellcharge_Id,
               Qu.New_Buycharge_Id,
               Qu.New_Lane_No,
               Qu.New_Version_No,--@@Added for the WPBN issues-146448,146968 on 19/12/08
               (Select Distinct Iqr.Charge_Description
                  From Qms_Quote_Rates Iqr
                 Where Iqr.Quote_Id = Qu.Quote_Id
                   And Iqr.Sell_Buy_Flag = Qu.Sell_Buy_Flag
                   And Iqr.Sellrate_Id = Qu.Old_Sellcharge_Id
                   And Iqr.Buyrate_Id = Qu.Old_Buycharge_Id
                   And Iqr.Rate_Lane_No = Qu.Old_Lane_No) Charge_Desc,
               (Select Distinct Iqr.Margin_Discount_Flag
                  From Qms_Quote_Rates Iqr
                 Where Iqr.Quote_Id = Qu.Quote_Id
                   And Iqr.Sell_Buy_Flag = Qu.Sell_Buy_Flag
                   And Iqr.Sellrate_Id = Qu.Old_Sellcharge_Id
                   And Iqr.Buyrate_Id = Qu.Old_Buycharge_Id
                   And Iqr.Rate_Lane_No = Qu.Old_Lane_No) Margin_Discount_Flag,
                   Qr.Charge_Description,
                   Qr.Margin_Discount_Flag,
               Qr.Margin_Type,
               Qr.Margin,
               Decode(Sd.Rate_Description,
                      'A FREIGHT RATE',
                      v_Discountforfreight,
                      null,
                      v_Discountforfreight,
                      0.0) v_Discountforfreight,
                     -- v_Discountforfreight,
               Qr.Discount_Type,
               Qr.Discount,
               Qr.break_point,
               --Sd.Line_No,
               Qr.Line_No,
               Sd.Buy_Rate_Amt,
               (Select Distinct Iqr.Charge_At
                  From Qms_Quote_Rates Iqr
                 Where Iqr.Quote_Id = Qu.Quote_Id
                   And Iqr.Sell_Buy_Flag = Qu.Sell_Buy_Flag
                   And Iqr.Sellrate_Id = Qu.Old_Sellcharge_Id
                   And Iqr.Buyrate_Id = Qu.Old_Buycharge_Id
                   And Iqr.Rate_Lane_No = Qu.Old_Lane_No) Charge_At,
                   Qr.charge_at,
               (Select Distinct Iqr.Serial_No
                  From Qms_Quote_Rates Iqr
                 Where Iqr.Quote_Id = Qu.Quote_Id
                   And Iqr.Sell_Buy_Flag = Qu.Sell_Buy_Flag
                   And Iqr.Sellrate_Id = Qu.Old_Sellcharge_Id
                   And Iqr.Buyrate_Id = Qu.Old_Buycharge_Id
                   And Iqr.Rate_Lane_No = Qu.Old_Lane_No) Serial_No,
                   Qr.Serial_No,
               Sd.Rate_Description,
            Sd.Frequency,
            Sd.Transit_Time,
           Qr.Carrier,
            Bd.Valid_Upto,
            Qr.Frequency_Checked,
               Qr.Carrier_Checked,
               Qr.Transit_Checked,
               Qr.Validity_Checked
          From Qms_Quotes_Updated Qu, Qms_Rec_Con_Sellratesdtl Sd,Qms_Buyrates_Dtl Bd,Qms_Quote_Rates Qr
         Where Qu.New_Sellcharge_Id = Sd.Rec_Con_Id
           And Qu.New_Buycharge_Id = Sd.Buyrateid
          And Qu.Quote_id = Qr.Quote_Id                 --@@Added for the WPBN issue-146968 on 02/01/09
         And Bd.Buyrateid=Qr.Buyrate_Id
        And Qr.Sellrate_Id=Sd.Rec_Con_Id
         ANd Bd.Lane_No=Qr.Rate_Lane_No
         And Qr.Line_No=Sd.Line_No
         And Bd.Weight_Break_Slab=Qr.Break_Point
           And Sd.Buyrateid=Bd.Buyrateid
           And Sd.Version_No=Bd.Version_No
           And Sd.Lane_No=Bd.Lane_No
           And Qu.New_Lane_No = Sd.Lane_No
         And Qu.New_version_no=(select new_version_no from qms_quotes_updated where quote_id=p_Quoteid and changedesc=p_Changedesc)
           And Qu.New_Version_No=Sd.Version_No--@@Added by kameswari for the WPBN issue-146448 on 24/12/08
         And Qu.Quote_Id = p_Quoteid
           And Qu.Changedesc = p_Changedesc order by Qr.Line_No;*/
      /*For i In (Select Qr.Break_Point,
                       Qr.Discount_Type,
                       Qr.Discount,
                       Qu.New_Sellcharge_Id,
                       Qu.New_Buycharge_Id,
                       Qu.New_Lane_No,
                       Qu.New_Version_No,--@@Added for the WPBN issues-146448,146968 on 19/12/08
                       Qr.Frequency_Checked,
                       Qr.Carrier_Checked,
                       Qr.Transit_Checked,
                       Qr.Validity_Checked--the WPBN issues-146448,146968 on 19/12/08
                  From Qms_Quote_Rates Qr, Qms_Quotes_Updated Qu
                 Where Qr.Sell_Buy_Flag = Qu.Sell_Buy_Flag
                   And Qr.Sellrate_Id = Qu.Old_Sellcharge_Id
                   And Qr.Buyrate_Id = Qu.Old_Buycharge_Id
                   And Qr.Rate_Lane_No = Qu.Old_Lane_No
                   And Qu.Quote_Id = Qr.Quote_Id
                   And Qu.Quote_Id = p_Quoteid
                   And Qu.Changedesc = p_Changedesc) Loop
        Update Qms_Quote_Rates_Temp
           Set Discount_Type = i.Discount_Type, Discount = i.Discount,
           FREQUENCY_CHECKED=i.frequency_checked,CARRIER_CHECKED=i.carrier_checked,
           TRANSIT_CHECKED=i.transit_checked,VALIDITY_CHECKED=i.validity_checked
         Where Quote_Id = p_Quoteid
           And Break_Point = i.Break_Point
           And Sell_Buy_Flag = p_Sellbuyflag
           And Sellrate_Id = i.New_Sellcharge_Id
           And Buyrate_Id = i.New_Buycharge_Id
           And Rate_Lane_No = i.New_Lane_No
            And Version_no=i.new_version_no; --@@Added by kameswari for the WPBN issue-146448 on 24/12/08

      End Loop;*/
    ELSIF (UPPER(p_Sellbuyflag) = 'B') THEN
      INSERT INTO QMS_QUOTE_RATES_TEMP
        (Quote_Id,
         Sell_Buy_Flag,
         Buyrate_Id,
         Charge_Id,
         Charge_Description,
         Margin_Discount_Flag,
         Margin_Type,
         Margin,
         Break_Point,
         Line_No,
         Buy_Rate,
         Charge_At)
        SELECT p_Quoteid,
               Qu.Sell_Buy_Flag,
               Qu.New_Buycharge_Id,
               (SELECT DISTINCT Iqr.Charge_Id
                  FROM QMS_QUOTE_RATES Iqr
                 WHERE Iqr.Quote_Id = Qu.Quote_Id
                   AND DECODE(Iqr.Sell_Buy_Flag,
                              'S',
                              DECODE(Iqr.Margin_Discount_Flag, 'M', 'B'),
                              Iqr.Sell_Buy_Flag) = Qu.Sell_Buy_Flag
                    AND Iqr.Buyrate_Id = Qu.Old_Buycharge_Id) Charge_Id,
               (SELECT DISTINCT Iqr.Charge_Description
                  FROM QMS_QUOTE_RATES Iqr
                 WHERE Iqr.Quote_Id = Qu.Quote_Id
                   AND DECODE(Iqr.Sell_Buy_Flag,
                              'S',
                              DECODE(Iqr.Margin_Discount_Flag, 'M', 'B'),
                              Iqr.Sell_Buy_Flag) = Qu.Sell_Buy_Flag
                    AND Iqr.Buyrate_Id = Qu.Old_Buycharge_Id
                   AND                            --@@Added by Kameswari for the WPBN issue-129343
                       Iqr.Charge_Description IN
                       (SELECT chargedescid
                          FROM QMS_CHARGE_GROUPSMASTER
                         WHERE chargegroup_id IN
                               (SELECT chargegroupid
                                  FROM QMS_QUOTE_CHARGEGROUPDTL
                                 WHERE quote_id = p_Quoteid)
                           AND inactivate = 'N')) Charge_Desc,
               (SELECT DISTINCT Iqr.Margin_Discount_Flag
                  FROM QMS_QUOTE_RATES Iqr
                 WHERE Iqr.Quote_Id = Qu.Quote_Id
                   AND DECODE(Iqr.Sell_Buy_Flag,
                              'S',
                              DECODE(Iqr.Margin_Discount_Flag, 'M', 'B'),
                              Iqr.Sell_Buy_Flag) = Qu.Sell_Buy_Flag
                   AND Iqr.Buyrate_Id = Qu.Old_Buycharge_Id) Margin_Discount_Flag,
               'P',
               v_Marginforcharges,
               Bd.Chargeslab,
               Bd.Lane_No,
               Bd.Chargerate,
               (SELECT DISTINCT Iqr.Charge_At
                  FROM QMS_QUOTE_RATES Iqr
                 WHERE Iqr.Quote_Id = Qu.Quote_Id
                   AND DECODE(Iqr.Sell_Buy_Flag,
                              'S',
                              DECODE(Iqr.Margin_Discount_Flag, 'M', 'B'),
                              Iqr.Sell_Buy_Flag) = Qu.Sell_Buy_Flag
                    AND Iqr.Buyrate_Id = Qu.Old_Buycharge_Id) Charge_At
          FROM QMS_QUOTES_UPDATED Qu, QMS_BUYCHARGESDTL Bd
         WHERE Qu.New_Buycharge_Id = Bd.Buysellchaegeid
           AND Qu.Quote_Id = p_Quoteid
           AND Qu.Changedesc = p_Changedesc;
      FOR i IN (SELECT Qr.Break_Point,
                       Qr.Margin_Type,
                       Qr.Margin,
                       Qu.New_Buycharge_Id
                  FROM QMS_QUOTE_RATES Qr, QMS_QUOTES_UPDATED Qu
                 WHERE DECODE(Qr.Sell_Buy_Flag,
                              'S',
                              DECODE(Qr.Margin_Discount_Flag, 'M', 'B'),
                              Qr.Sell_Buy_Flag) = Qu.Sell_Buy_Flag
                   AND Qr.Buyrate_Id = Qu.Old_Buycharge_Id
                   AND Qu.Quote_Id = Qr.Quote_Id
                   AND Qu.Quote_Id = p_Quoteid
                   AND Qu.Changedesc = p_Changedesc) LOOP
        UPDATE QMS_QUOTE_RATES_TEMP
           SET Margin_Type = i.Margin_Type, Margin = i.Margin
         WHERE Quote_Id = p_Quoteid
           AND Break_Point = i.Break_Point
           AND Sell_Buy_Flag = p_Sellbuyflag
           AND Buyrate_Id = i.New_Buycharge_Id;
      END LOOP;
    ELSIF (UPPER(p_Sellbuyflag) = 'S') THEN
      INSERT INTO QMS_QUOTE_RATES_TEMP
        (Quote_Id,
         Sell_Buy_Flag,
         Sellrate_Id,
         Buyrate_Id,
         Charge_Id,
         Charge_Description,
         Margin_Discount_Flag,
         Margin_type,
         Margin,
         Discount_Type,
         Discount,
         Break_Point,
         Line_No,
         Buy_Rate,
         Charge_At)
        SELECT p_Quoteid,
               Qu.Sell_Buy_Flag,
               Qu.New_Sellcharge_Id,
               Qu.New_Buycharge_Id,
               (SELECT DISTINCT Iqr.Charge_Id
                  FROM QMS_QUOTE_RATES Iqr
                 WHERE Iqr.Quote_Id = Qu.Quote_Id
                      /*AND DECODE (iqr.sell_buy_flag,
                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                          'S', DECODE
                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                  (iqr.margin_discount_flag,
                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                   'M', 'B','S'
                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                  ),
                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                          iqr.sell_buy_flag
                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                         ) = qu.sell_buy_flag*/
                   AND Iqr.Sell_Buy_Flag = Qu.Sell_Buy_Flag
                   AND Iqr.Buyrate_Id = Qu.Old_Buycharge_Id
                   AND Iqr.Sellrate_Id = Qu.Old_Sellcharge_Id) Charge_Id,
               (SELECT DISTINCT Iqr.Charge_Description
                  FROM QMS_QUOTE_RATES Iqr
                 WHERE Iqr.Quote_Id = Qu.Quote_Id
                   AND Iqr.Sell_Buy_Flag = Qu.Sell_Buy_Flag
                   AND Iqr.Sellrate_Id = Qu.Old_Sellcharge_Id --@@Added by Kameswari for the WPBN issue-129343
                   AND Iqr.Buyrate_Id = Qu.Old_Buycharge_Id
                   AND Iqr.Charge_Description IN
                       (SELECT chargedescid
                          FROM QMS_CHARGE_GROUPSMASTER
                         WHERE chargegroup_id IN
                               (SELECT chargegroupid
                                  FROM QMS_QUOTE_CHARGEGROUPDTL
                                 WHERE quote_id = p_Quoteid)
                           AND inactivate = 'N')) Charge_Desc,
               (SELECT DISTINCT Iqr.Margin_Discount_Flag
                  FROM QMS_QUOTE_RATES Iqr
                 WHERE Iqr.Quote_Id = Qu.Quote_Id
                   AND Iqr.Sell_Buy_Flag = Qu.Sell_Buy_Flag
                   AND Iqr.Sellrate_Id = Qu.Old_Sellcharge_Id
                   AND Iqr.Buyrate_Id = Qu.Old_Buycharge_Id) Margin_Discount_Flag,
               'P',
               v_Marginforcharges,
               'P',
               v_Discountforcharges,
               Sd.Chargeslab,
               Sd.Lane_No,
               Sd.Chargerate,
               (SELECT DISTINCT Iqr.Charge_At
                  FROM QMS_QUOTE_RATES Iqr
                 WHERE Iqr.Quote_Id = Qu.Quote_Id
                   AND Iqr.Sell_Buy_Flag = Qu.Sell_Buy_Flag
                   AND Iqr.Sellrate_Id = Qu.Old_Sellcharge_Id
                   AND Iqr.Buyrate_Id = Qu.Old_Buycharge_Id) Charge_At
          FROM QMS_QUOTES_UPDATED    Qu,
               QMS_SELLCHARGESDTL    Sd,
               QMS_SELLCHARGESMASTER Sm
         WHERE Qu.New_Sellcharge_Id = Sd.Sellchargeid
           AND Sm.Sellchargeid = Sd.Sellchargeid
           AND Qu.New_Buycharge_Id = Sm.Buychargeid
           AND Qu.Quote_Id = p_Quoteid
           AND Qu.Changedesc = p_Changedesc;
      FOR i IN (SELECT Qr.Break_Point,
                        Qr.Margin_Type,
                        Qr.Margin,
                       Qr.Discount_Type,
                       Qr.Discount,
                       Qu.New_Sellcharge_Id,
                       Qu.New_Buycharge_Id
                  FROM QMS_QUOTE_RATES Qr, QMS_QUOTES_UPDATED Qu
                 WHERE Qr.Sell_Buy_Flag = Qu.Sell_Buy_Flag
                   AND Qr.Sellrate_Id = Qu.Old_Sellcharge_Id
                   AND Qr.Buyrate_Id = Qu.Old_Buycharge_Id
                   AND Qu.Quote_Id = Qr.Quote_Id
                   AND Qu.Quote_Id = p_Quoteid
                   AND Qu.Changedesc = p_Changedesc) LOOP
        UPDATE QMS_QUOTE_RATES_TEMP
           SET margin_type=i.margin_type,Margin=i.margin,Discount_Type = i.Discount_Type, Discount = i.Discount
         WHERE Quote_Id = p_Quoteid
           AND Break_Point = i.Break_Point
           AND Sell_Buy_Flag = p_Sellbuyflag
           AND Sellrate_Id = i.New_Sellcharge_Id
           AND Buyrate_Id = i.New_Buycharge_Id;
      END LOOP;
    ELSIF (UPPER(p_Sellbuyflag) = 'BC') THEN
      INSERT INTO QMS_QUOTE_RATES_TEMP
        (Quote_Id,
         Sell_Buy_Flag,
         Buyrate_Id,
         Charge_Description,
         Margin_Discount_Flag,
         Margin_Type,
         Margin,
         Break_Point,
         Line_No,
         Buy_Rate,
         Charge_At)
        SELECT p_Quoteid,
               Qu.Sell_Buy_Flag,
               Qu.New_Buycharge_Id,
               (SELECT DISTINCT Iqr.Charge_Description
                  FROM QMS_QUOTE_RATES Iqr
                 WHERE Iqr.Quote_Id = Qu.Quote_Id
                   AND DECODE(Iqr.Sell_Buy_Flag,
                              'SC',
                              DECODE(Iqr.Margin_Discount_Flag, 'M', 'BC'),
                              Iqr.Sell_Buy_Flag) = Qu.Sell_Buy_Flag
                    AND Iqr.Buyrate_Id = Qu.Old_Buycharge_Id
                   AND Iqr.Charge_At = Qu.Charge_At) Charge_Desc,
               (SELECT DISTINCT Iqr.Margin_Discount_Flag
                  FROM QMS_QUOTE_RATES Iqr
                 WHERE Iqr.Quote_Id = Qu.Quote_Id
                   AND DECODE(Iqr.Sell_Buy_Flag,
                              'SC',
                              DECODE(Iqr.Margin_Discount_Flag, 'M', 'BC'),
                              Iqr.Sell_Buy_Flag) = Qu.Sell_Buy_Flag
                   AND Iqr.Buyrate_Id = Qu.Old_Buycharge_Id
                   AND Iqr.Charge_At = Qu.Charge_At) Margin_Discount_Flag,
               'P',
               v_Marginforcartage,
               Bd.Chargeslab,
               Bd.Line_No,
               Bd.Chargerate,
               (SELECT DISTINCT Iqr.Charge_At
                  FROM QMS_QUOTE_RATES Iqr
                 WHERE Iqr.Quote_Id = Qu.Quote_Id
                   AND DECODE(Iqr.Sell_Buy_Flag,
                              'SC',
                              DECODE(Iqr.Margin_Discount_Flag, 'M', 'BC'),
                              Iqr.Sell_Buy_Flag) = Qu.Sell_Buy_Flag
                   AND Iqr.Buyrate_Id = Qu.Old_Buycharge_Id
                   AND Iqr.Charge_At = Qu.Charge_At) Charge_At
          FROM QMS_QUOTES_UPDATED Qu, QMS_CARTAGE_BUYDTL Bd
         WHERE Qu.New_Buycharge_Id = Bd.Cartage_Id
           AND Qu.Zone_Code = Bd.Zone_Code
           AND Qu.Charge_At = Bd.Charge_Type
           AND Qu.Quote_Id = p_Quoteid
           AND Qu.Changedesc = p_Changedesc;
      FOR i IN (SELECT Qr.Break_Point,
                       Qr.Margin_Type,
                       Qr.Margin,
                       Qu.New_Buycharge_Id,
                       Qu.Charge_At
                  FROM QMS_QUOTE_RATES    Qr,
                       QMS_QUOTE_MASTER   Qm,
                       QMS_QUOTES_UPDATED Qu
                 WHERE DECODE(Qr.Sell_Buy_Flag,
                              'SC',
                              DECODE(Qr.Margin_Discount_Flag, 'M', 'BC'),
                              Qr.Sell_Buy_Flag) = Qu.Sell_Buy_Flag
                   AND Qr.Buyrate_Id = Qu.Old_Buycharge_Id
                   AND Qu.Quote_Id = Qr.Quote_Id
                   AND Qu.Quote_Id = Qm.Id
                   AND Qu.Zone_Code =
                       DECODE(Qr.Charge_At,
                              'Pickup',
                              Qm.Shipperzones,
                              'Delivery',
                              Qm.Consigneezones)
                   AND Qu.Charge_At = Qr.Charge_At
                   AND Qu.Quote_Id = p_Quoteid
                   AND Qu.Changedesc = p_Changedesc) LOOP
        UPDATE QMS_QUOTE_RATES_TEMP
           SET Margin_Type = i.Margin_Type, Margin = i.Margin
         WHERE Quote_Id = p_Quoteid
           AND Break_Point = i.Break_Point
           AND Sell_Buy_Flag = p_Sellbuyflag
           AND Buyrate_Id = i.New_Buycharge_Id
           AND Charge_At = i.Charge_At;
      END LOOP;
    ELSIF (UPPER(p_Sellbuyflag) = 'SC') THEN
      INSERT INTO QMS_QUOTE_RATES_TEMP
        (Quote_Id,
         Sell_Buy_Flag,
         Sellrate_Id,
         Buyrate_Id,
         Charge_Description,
         Margin_Discount_Flag,
         Margin_type,
         Margin,
         Discount_Type,
         Discount,
         Break_Point,
         Line_No,
         Buy_Rate,
         Charge_At)
        SELECT p_Quoteid,
               Qu.Sell_Buy_Flag,
               Qu.New_Sellcharge_Id,
               Qu.New_Buycharge_Id,
               (SELECT DISTINCT Iqr.Charge_Description
                  FROM QMS_QUOTE_RATES Iqr
                 WHERE Iqr.Quote_Id = Qu.Quote_Id
                   AND Iqr.Sell_Buy_Flag = Qu.Sell_Buy_Flag
                   AND Iqr.Sellrate_Id = Qu.Old_Sellcharge_Id
                   AND Iqr.Buyrate_Id = Qu.Old_Buycharge_Id
                   AND Iqr.Charge_At = Qu.Charge_At) Charge_Desc,
               (SELECT DISTINCT Iqr.Margin_Discount_Flag
                  FROM QMS_QUOTE_RATES Iqr
                 WHERE Iqr.Quote_Id = Qu.Quote_Id
                   AND Iqr.Sell_Buy_Flag = Qu.Sell_Buy_Flag
                   AND Iqr.Sellrate_Id = Qu.Old_Sellcharge_Id
                   AND Iqr.Buyrate_Id = Qu.Old_Buycharge_Id
                   AND Iqr.Charge_At = Qu.Charge_At) Margin_Discount_Flag,
               'P',
               v_Marginforcartage,
               'P',
               v_Discountforcartage,
               Sd.Chargeslab,
               Sd.Line_No,
               Sd.Buyrate_Amt,
               (SELECT DISTINCT Iqr.Charge_At
                  FROM QMS_QUOTE_RATES Iqr
                 WHERE Iqr.Quote_Id = Qu.Quote_Id
                   AND Iqr.Sell_Buy_Flag = Qu.Sell_Buy_Flag
                   AND Iqr.Sellrate_Id = Qu.Old_Sellcharge_Id
                   AND Iqr.Buyrate_Id = Qu.Old_Buycharge_Id
                   AND Iqr.Charge_At = Qu.Charge_At) Charge_At
          FROM QMS_QUOTES_UPDATED Qu, QMS_CARTAGE_SELLDTL Sd
         WHERE Qu.New_Buycharge_Id = Sd.Cartage_Id
           AND Qu.New_Sellcharge_Id = Sd.Sell_Cartage_Id
           AND Qu.Zone_Code = Sd.Zone_Code
           AND Qu.Charge_At = Sd.Charge_Type
           AND Sd.Activeinactive = 'A'
           AND Qu.Quote_Id = p_Quoteid
           AND Qu.Changedesc = p_Changedesc;
      FOR i IN (SELECT Qr.Break_Point,
                      Qr.Margin_Type,
                        Qr.Margin,
                       Qr.Discount_Type,
                       Qr.Discount,
                       Qu.New_Sellcharge_Id,
                       Qu.New_Buycharge_Id,
                       Qu.Charge_At
                  FROM QMS_QUOTE_RATES    Qr,
                       QMS_QUOTE_MASTER   Qm,
                       QMS_QUOTES_UPDATED Qu
                 WHERE Qr.Sell_Buy_Flag = Qu.Sell_Buy_Flag
                   AND Qr.Buyrate_Id = Qu.Old_Buycharge_Id
                   AND Qr.Sellrate_Id = Qu.Old_Sellcharge_Id
                   AND Qu.Quote_Id = Qr.Quote_Id
                   AND Qu.Quote_Id = Qm.Id
                   AND Qu.Zone_Code =
                       DECODE(Qr.Charge_At,
                              'Pickup',
                              Qm.Shipperzones,
                              'Delivery',
                              Qm.Consigneezones)
                   AND Qu.Charge_At = Qr.Charge_At
                   AND Qu.Quote_Id = p_Quoteid
                   AND Qu.Changedesc = p_Changedesc) LOOP
        UPDATE QMS_QUOTE_RATES_TEMP
           SET margiN_type=i.margin_type,
           margin=i.margin,
           Discount_Type = i.Discount_Type, Discount = i.Discount
         WHERE Quote_Id = p_Quoteid
           AND Break_Point = i.Break_Point
           AND Sell_Buy_Flag = p_Sellbuyflag
           AND Buyrate_Id = i.New_Buycharge_Id
           AND Sellrate_Id = i.New_Sellcharge_Id
           AND Charge_At = i.Charge_At;
      END LOOP;
    END IF;
    SELECT Quote_Id, terminal_id
      INTO v_Quoteid, v_terminalid
      FROM QMS_QUOTE_MASTER
     WHERE Id = p_Quoteid;
     --@@Added by Kameswari for the WBN issue-154398 on 19/02/09
     IF UPPER(p_Sellbuyflag) = 'B'
     THEN
        SELECT  qu.new_buycharge_id,qu.old_buycharge_id INTO v_newchargeid,v_oldchargeid FROM  QMS_QUOTES_UPDATED qu WHERE qu.sell_buy_flag='B' AND qu.quote_id=p_Quoteid AND qu.changedesc=p_Changedesc;
         SELECT qbm.chargebasis,qbm.rate_break INTO v_oldchargebasis,v_oldwtbreak FROM QMS_BUYSELLCHARGESMASTER qbm   WHERE qbm.buysellchargeid=v_oldchargeid;
         SELECT qbm.chargebasis,qbm.rate_break INTO v_newchargebasis,v_newwtbreak FROM QMS_BUYSELLCHARGESMASTER qbm   WHERE qbm.buysellchargeid=v_newchargeid;
       IF(v_oldchargebasis<>v_newchargebasis OR v_oldwtbreak<>v_newwtbreak)
       THEN
           UPDATE QMS_QUOTE_RATES_TEMP SET change_flag='Y',margin='0.0',discount='0.0' WHERE buyrate_id=v_newchargeid AND sell_buy_flag='B' AND quote_id=p_Quoteid;
       END IF;
     END IF;
        IF UPPER(p_Sellbuyflag) = 'S'
     THEN
        SELECT  qu.new_sellcharge_id,qu.old_sellcharge_id INTO v_newchargeid,v_oldchargeid FROM  QMS_QUOTES_UPDATED qu WHERE qu.sell_buy_flag='S' AND qu.quote_id=p_Quoteid AND qu.changedesc=p_Changedesc;
         SELECT qbm.chargebasis,qbm.rate_break INTO v_oldchargebasis,v_oldwtbreak FROM QMS_SELLCHARGESMASTER qbm   WHERE qbm.sellchargeid=v_oldchargeid;
         SELECT qbm.chargebasis,qbm.rate_break INTO v_newchargebasis,v_newwtbreak FROM QMS_SELLCHARGESMASTER qbm   WHERE qbm.sellchargeid=v_newchargeid;
       IF(v_oldchargebasis<>v_newchargebasis OR v_oldwtbreak<>v_newwtbreak)
       THEN
           UPDATE QMS_QUOTE_RATES_TEMP SET change_flag='Y',margin='0.0',discount='0.0' WHERE sellrate_id=v_newchargeid AND sell_buy_flag='S' AND quote_id=p_Quoteid;
       END IF;
     END IF;

     --@@WPBN issue-154398
     --@@Added on 04Feb2011
     SELECT is_multi_quote INTO v_is_multi_quote FROM QMS_QUOTE_MASTER
            WHERE quote_id = v_Quoteid AND id=p_Quoteid;
    IF v_is_multi_quote = 'Y'
     THEN
     Qms_Quote_Pack.Updated_Multi_Quote_Inf_Modify(p_Quoteid,
                                             v_Quoteid,
                                            p_Sellbuyflag,
                                             v_terminalid,
                                             p_Rs,
                                             p_Rs1,
                                             p_Rs2,
                                             p_Rs3);
     ELSE     --Ended on 04Feb2011
    Qms_Quote_Pack.Updated_Quote_Info_Modify(v_Quoteid,
                                            p_Sellbuyflag,
                                             v_terminalid,
                                             p_Rs,
                                             p_Rs1,
                                             p_Rs2,
                                             p_Rs3);
      END IF;
  END;

END Qms_Quotepack_New;

/

/
