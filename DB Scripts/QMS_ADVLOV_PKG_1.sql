--------------------------------------------------------
--  DDL for Package Body QMS_ADVLOV_PKG
--------------------------------------------------------

  CREATE OR REPLACE PACKAGE BODY "QMS_ADVLOV_PKG" AS



    PROCEDURE get_lov(

        p_terminal_id              IN  VARCHAR2 DEFAULT NULL,

        p_lov_id                   IN  VARCHAR2,

        p_where                    IN  VARCHAR2 DEFAULT NULL,

        p_page_no                  IN  NUMBER DEFAULT 1,

        p_title                    OUT VARCHAR2,

        p_pagination               OUT VARCHAR2,

        p_page_rows                OUT NUMBER,

        p_tot_col                  OUT NUMBER,

        p_tot_rec                  OUT NUMBER,

        p_width                    OUT NUMBER,

        p_height                   OUT NUMBER,

        p_col_rec                  OUT resultset,

        p_val_rec                  OUT resultset)

    IS

        CURSOR c1

        IS

            SELECT id

            FROM   FS_LOV_COLUMNS

            WHERE  lov_id = p_lov_id

            AND    visible = 'Y'

            ORDER BY position;



        v_table_name      FS_LIST_OF_VALUES.table_name%TYPE;

        v_terminal_id     FS_LIST_OF_VALUES.reference_1%TYPE;

        v_mul             VARCHAR2(1);

        v_sql_stmt        VARCHAR2(2000);

        v_pos             NUMBER;

        v_record_group    VARCHAR2(4000);

    BEGIN

        BEGIN

            SELECT title, pagination, no_of_records, table_name, reference_1, width, height

            INTO   p_title, p_pagination, p_page_rows, v_table_name, v_terminal_id, p_width, p_height

            FROM   FS_LIST_OF_VALUES

            WHERE  id = p_lov_id;
--@@ Added by subrahmanyam for the WPBN ISSUE: 146436 On 16/12/2008
             IF(p_lov_id ='CHARGEBASIS_MASTER') THEN
                       SELECT COUNT(*) INTO p_page_rows FROM v_chargebasis_master;
            END IF;
--@@ Ended by subrahmanyam for the WPBN ISSUE:- 146436 On 16/12/2008
            dbms_output.put_line('in get _lov table name'||v_table_name);

        END;



           OPEN p_col_rec

             FOR

                 SELECT id column_id, default_label description, width

                 FROM   FS_LOV_COLUMNS

                 WHERE  lov_id = p_lov_id

                 AND    visible = 'Y'

                 ORDER BY position;

        BEGIN

            SELECT COUNT(1)

            INTO   p_tot_col

            FROM   FS_LOV_COLUMNS

            WHERE  lov_id = p_lov_id

            AND    visible = 'Y';

        END;

        FOR r1 IN c1 LOOP
        -- Added By Kishore For trimming the last 3 Charaacters in the Sur Charge Description
           IF (p_lov_id = 'SURCHARGE_LOV') THEN

              IF (r1.id = 'SURCHARGE_DESC') THEN
                 v_record_group := v_record_group || 'substr('||r1.id||', 0, (length('||r1.id||')-3)) SURCHARGE_DESC,';
                 dbms_output.put_line('in get _lov v_record_group name111111'||v_record_group);
              ELSE
                  v_record_group := v_record_group || r1.id || ',';
                 dbms_output.put_line('in get _lov v_record_group name111111'||v_record_group);

              END IF;

            ELSE
              v_record_group := v_record_group || r1.id || ',';
             dbms_output.put_line('in get _lov v_record_group name111111'||v_record_group);
            END IF;
        -- End Of Kishore For trimming the last 3 Charaacters in the Sur Charge Description
        END LOOP;

        v_record_group := 'Select ' || SUBSTR(v_record_group, 1, INSTR(v_record_group, ',', -1) - 1) || ' from ' || v_table_name

                          || ' ';
                          dbms_output.put_line('in get _lov v_record_group name 222222'||v_record_group);

        /*if v_terminal_id is not null then

            v_record_group := v_record_group || ' Where ' || v_terminal_id || ' = ' || chr(39) || p_terminal_id || chr(39);

        end if;*/

     IF p_where IS NOT NULL THEN

            IF INSTR(UPPER(p_where), 'WHERE ') <> 0 THEN

                IF v_terminal_id IS NOT NULL THEN

                    v_record_group := v_record_group || ' and ' || SUBSTR(p_where, INSTR(UPPER(p_where), 'WHERE ') + 6);
                    dbms_output.put_line('in get _lov v_record_group name333333'||v_record_group);

                ELSE

                    v_record_group := v_record_group || p_where;
                    dbms_output.put_line('in get _lov v_record_group name 4444444'||v_record_group);

                END IF;

            ELSIF INSTR(UPPER(p_where), 'ORDER BY ') <> 0 THEN

                v_record_group := v_record_group || ' ' || p_where;
                dbms_output.put_line('in get _lov v_record_group name 55555555'||v_record_group);

            END IF;

        END IF;

        IF p_pagination = 'Y' THEN

            v_sql_stmt := 'SELECT * FROM (SELECT t1.*, ROWNUM rn FROM (' || v_record_group || ') t1';
dbms_output.put_line('in get _lov v_record_group name 66666666'||v_sql_stmt);
            v_sql_stmt :=

                   v_sql_stmt

                || ' WHERE ROWNUM <= ('

                || (p_page_no - 1) * p_page_rows

                || ') + '

                || p_page_rows

                || ') WHERE  rn > ('

                || (p_page_no - 1) * p_page_rows

                || ')';
                dbms_output.put_line('in get _lov v_record_group name 77777'||v_sql_stmt);


        ELSE

            v_sql_stmt := v_record_group;
            dbms_output.put_line('in get _lov v_record_group name 8888888888888'||v_sql_stmt);


        END IF;

        OPEN p_val_rec

         FOR v_sql_stmt;

         print_out(v_sql_stmt);

--        DBMS_OUTPUT.PUT_LINE (v_sql_stmt );

        v_record_group := 'SELECT COUNT(1) ' || SUBSTR(v_record_group, INSTR(UPPER(v_record_group), ' FROM '));

 dbms_output.put_line('in get _lov v_record_group name'||v_record_group);
        --print_out(v_record_group);


        EXECUTE IMMEDIATE v_record_group

        INTO   p_tot_rec;

    END;



END;

/

/
