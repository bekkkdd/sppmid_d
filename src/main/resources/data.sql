insert into roles (id, created_at, deleted_at, updated_at, description, role)
VALUES (nextval('s_roles'), now(), null, null, 'Супер Администратор', 'ROLE_SUPER_ADMIN'),
       (nextval('s_roles'), now(), null, null, 'Администратор', 'ROLE_ADMIN'),
       (nextval('s_roles'), now(), null, null, 'Пользователь', 'ROLE_USER'),
       (nextval('s_roles'), now(), null, null, 'Заявитель', 'ROLE_ZAYAVITEL'),
       (nextval('s_roles'), now(), null, null, 'Руководитель ГО', 'ROLE_RUKOVODITEL_GO'),
       (nextval('s_roles'), now(), null, null, 'Ответственный секретарь ГО', 'ROLE_OTVESTVENNIY_SEKRETAR_GO'),
       (nextval('s_roles'), now(), null, null, 'Исполнитель Политического департамента МИД РК', 'ROLE_ISPOLNITEL_POLIT_DEP_MID'),
       (nextval('s_roles'), now(), null, null, 'Руководитель Политического департамента МИД РК', 'ROLE_RUKOVODITEL_POLIT_DEP_MID'),
       (nextval('s_roles'), now(), null, null, 'Исполнитель УБП МИД РК', 'ROLE_ISPOLNITEL_UBP_MID'),
       (nextval('s_roles'), now(), null, null, 'Исполнитель УЗК МИД РК', 'ROLE_ISPOLNITEL_UZK_MID'),
       (nextval('s_roles'), now(), null, null, 'Руководитель ВФД', 'ROLE_RUKOVODITEL_VFD'),
       (nextval('s_roles'), now(), null, null, 'Исполнитель ДКС МИД РК', 'ROLE_ISPOLNITEL_DKS_MID'),
       (nextval('s_roles'), now(), null, null, 'Руководитель ДКС', 'ROLE_RUKOVODITEL_DKS_MID'),
       (nextval('s_roles'), now(), null, null, 'Ответственный секретарь МИД РК', 'ROLE_OTVESTVENNIY_SEKRETAR_MID'),
       (nextval('s_roles'), now(), null, null, 'Исполниель ВФД', 'ROLE_ISPOLNITEL_VFD');


insert into groups (id, created_at, deleted_at, updated_at, name, description)
values (nextval('s_groups'), now(), null, null, 'Супер Администратор', 'Супер Глобальный Администратор'),
       (nextval('s_groups'), now(), null, null, 'Администратор СПП', 'Администратор СПП'),
       (nextval('s_groups'), now(), null, null, 'Заявитель', 'Заявитель (командируемый сотрудник ГО)'),
       (nextval('s_groups'), now(), null, null, 'Руководитель ГО', 'Руководитель ГО (начальник Управления, заместитель директора, директор Департамента)'),
       (nextval('s_groups'), now(), null, null, 'Ответственный секретарь ГО', 'Ответственный секретарь ГО'),
       (nextval('s_groups'), now(), null, null, 'Исполнитель Политического департамента МИД РК', 'Исполнитель Политического департамента МИД РК (ответственный сотрудник)'),
       (nextval('s_groups'), now(), null, null, 'Руководитель Политического департамента МИД РК', 'Руководитель Политического департамента МИД РК (начальник Управления, заместитель директора, директор Департамента)'),
       (nextval('s_groups'), now(), null, null, 'Исполнитель УБП МИД РК', 'Исполнитель УБП МИД РК (начальник Управления, сотрудник Управления бюджетного планирования ВФД)'),
       (nextval('s_groups'), now(), null, null, 'Исполнитель УЗК МИД РК', 'Исполнитель УЗК МИД РК (начальник Управления, сотрудник Управления заграничных командировок ВФД)'),
       (nextval('s_groups'), now(), null, null, 'Руководитель ВФД', 'Руководитель ВФД (заместитель директора, директор Департамента)'),
       (nextval('s_groups'), now(), null, null, 'Исполнитель ДКС МИД РК', 'Исполнитель ДКС МИД РК (ответственный сотрудник)'),
       (nextval('s_groups'), now(), null, null, 'Руководитель ДКС', 'Руководитель ДКС (начальник Управления, заместитель директора, директор Департамента)'),
       (nextval('s_groups'), now(), null, null, 'Ответственный секретарь МИД РК', 'Ответственный секретарь МИД РК'),
       (nextval('s_groups'), now(), null, null, 'Исполниель ВФД', 'Исполниель ВФД');

insert into groups_roles(group_id, role_id)
VALUES (1, 1),
       (1, 3),
       (2, 3),
       (2, 2),
       (3, 3),
       (3, 4),
       (4, 3),
       (4, 5),
       (5, 3),
       (5, 6),
       (6, 3),
       (6, 7),
       (7, 3),
       (7, 8),
       (8, 3),
       (8, 9),
       (9, 3),
       (9, 10),
       (10, 3),
       (10, 11),
       (11, 3),
       (11, 12),
       (12, 3),
       (12, 13),
       (13, 3),
       (13, 14),
       (14, 15);

INSERT INTO organizations(id, created_at, deleted_at, updated_at, name, is_mid_rk)
VALUES (nextval('s_organizations'), now(), null, null, 'AO НИТ', 0),
       (nextval('s_organizations'), now(), null, null, 'Администрация Президента РК', 0),
       (nextval('s_organizations'), now(), null, null, 'Канцелярия Премьер-Министра РК', 0),
       (nextval('s_organizations'), now(), null, null, 'Генеральная прокуратура РК', 0),
       (nextval('s_organizations'), now(), null, null, 'Верховный суд РК', 0),
       (nextval('s_organizations'), now(), null, null, 'Комитет Национальной безопасности РК', 0),
       (nextval('s_organizations'), now(), null, null, 'Конституционный Совет РК', 0),
       (nextval('s_organizations'), now(), null, null, 'Агентство РК по делам государственной службы', 0),
       (nextval('s_organizations'), now(), null, null, 'Агентство РК по противодействию коррупции', 0),
       (nextval('s_organizations'), now(), null, null, 'Министерство внутренних дел РК', 0),
       (nextval('s_organizations'), now(), null, null, 'Министерство здравоохранения РК', 0),
       (nextval('s_organizations'), now(), null, null, 'Министерство труда и социальной защиты РК', 0),
       (nextval('s_organizations'), now(), null, null, 'Министерство культуры и спорта РК', 0),
       (nextval('s_organizations'), now(), null, null, 'Министерство национальной экономики РК', 0),
       (nextval('s_organizations'), now(), null, null, 'Министерство цифрового развития, инноваций и аэрокосмической промышленности РК', 0),
       (nextval('s_organizations'), now(), null, null, 'Министерство обороны РК', 0),
       (nextval('s_organizations'), now(), null, null, 'Министерство образования и науки РК', 0),
       (nextval('s_organizations'), now(), null, null, 'Министерство информации и общественного равзития РК', 0),
       (nextval('s_organizations'), now(), null, null, 'Министерство по индустрии и инфраструктурного развитию РК', 0),
       (nextval('s_organizations'), now(), null, null, 'Министерство сельского хозяйства РК', 0),
       (nextval('s_organizations'), now(), null, null, 'Министерство финансов РК', 0),
       (nextval('s_organizations'), now(), null, null, 'Министерство энергетики РК', 0),
       (nextval('s_organizations'), now(), null, null, 'Министерство юстиции РК', 0),
       (nextval('s_organizations'), now(), null, null, 'Министерство энергетики РК', 0),
       (nextval('s_organizations'), now(), null, null, 'Министерство иностранных дел РК', 0),
       (nextval('s_organizations'), now(), null, null, 'Министерство экологии, геологии и природных ресурсов РК', 0),
       (nextval('s_organizations'), now(), null, null, 'Министерство торговли и интеграции РК', 0),
       (nextval('s_organizations'), now(), null, null, 'Национальный центр по правам человека РК', 0),
       (nextval('s_organizations'), now(), null, null, 'Служба внешней разведки «Сырбар» РК', 0),
       (nextval('s_organizations'), now(), null, null, 'Служба государственной охраны РК', 0),
       (nextval('s_organizations'), now(), null, null, 'Центральная избирательная комиссия РК', 0),
       (nextval('s_organizations'), now(), null, null, 'Управление Делами Президента РК', 0),
       (nextval('s_organizations'), now(), null, null, 'Канцелярия Министра', 1),
       (nextval('s_organizations'), now(), null, null, 'Комитет международной информации', 1),
       (nextval('s_organizations'), now(), null, null, 'Департамент внешнего анализа и прогнозирования', 1),
       (nextval('s_organizations'), now(), null, null, 'Департамент внешнеэкономической политики', 1),
       (nextval('s_organizations'), now(), null, null, 'Департамент администрации и контроля', 1),
       (nextval('s_organizations'), now(), null, null, 'Служба государственного протокола', 1),
       (nextval('s_organizations'), now(), null, null, 'Департамент СНГ', 1),
       (nextval('s_organizations'), now(), null, null, 'Департамент евразийской интеграции', 1),
       (nextval('s_organizations'), now(), null, null, 'Департамент Азии и Африки', 1),
       (nextval('s_organizations'), now(), null, null, 'Департамент общеазиатского сотрудничества', 1),
       (nextval('s_organizations'), now(), null, null, 'Департамент Европы', 1),
       (nextval('s_organizations'), now(), null, null, 'Департамент Америки', 1),
       (nextval('s_organizations'), now(), null, null, 'Департамент многостороннего сотрудничества', 1),
       (nextval('s_organizations'), now(), null, null, 'Международно-правовой департамент', 1),
       (nextval('s_organizations'), now(), null, null, 'Департамент консульской службы', 1),
       (nextval('s_organizations'), now(), null, null, 'Валютно-финансовый департамент', 1),
       (nextval('s_organizations'), now(), null, null, 'Департамент материально-технического обеспечения', 1),
       (nextval('s_organizations'), now(), null, null, 'Постоянное представительство в городе Алматы', 1);

INSERT INTO departments (id, created_at, deleted_at, updated_at, name, organization_id) VALUES (nextval('s_departments'), '2020-01-21 01:18:23.655196', NULL, NULL, 'Администрация НИТ', 1);
INSERT INTO departments (id, created_at, deleted_at, updated_at, name, organization_id) VALUES (nextval('s_departments'), '2020-01-21 01:32:09.087', NULL, NULL, 'Администрация', 13);
INSERT INTO departments (id, created_at, deleted_at, updated_at, name, organization_id) VALUES (nextval('s_departments'), '2020-01-21 01:45:22.116', NULL, NULL, 'Администрации и управления', 43);
INSERT INTO departments (id, created_at, deleted_at, updated_at, name, organization_id) VALUES (nextval('s_departments'), '2020-01-21 02:46:45.43', NULL, NULL, 'Управление персоналом', 13);
INSERT INTO departments (id, created_at, deleted_at, updated_at, name, organization_id) VALUES (nextval('s_departments'), '2020-01-21 03:50:38.655', NULL, '2020-01-21 03:53:44.725', 'Департамент Гос. Закупок', 43);
INSERT INTO departments (id, created_at, deleted_at, updated_at, name, organization_id) VALUES (nextval('s_departments'), '2020-01-22 05:35:34.437', NULL, NULL, 'Администрация', 17);
INSERT INTO departments (id, created_at, deleted_at, updated_at, name, organization_id) VALUES (nextval('s_departments'), '2020-01-22 16:39:35.017', NULL, NULL, 'Администрация и управление', 11);
INSERT INTO departments (id, created_at, deleted_at, updated_at, name, organization_id) VALUES (nextval('s_departments'), '2020-01-23 14:20:06.126', NULL, NULL, 'Администрация', 2);
INSERT INTO departments (id, created_at, deleted_at, updated_at, name, organization_id) VALUES (nextval('s_departments'), '2020-01-23 21:55:02.724', NULL, NULL, 'Администрация', 40);
INSERT INTO departments (id, created_at, deleted_at, updated_at, name, organization_id) VALUES (nextval('s_departments'), '2020-01-23 21:55:27.008', NULL, NULL, 'Тех. Поддержка', 40);

INSERT INTO users (id, created_at, deleted_at, updated_at, login, password, department_id, group_id, firstname, lastname, middle_name, email)
VALUES (nextval('s_users'), '2020-01-21 01:18:23.656626', NULL, NULL, 'admin', '$2y$12$dWpKho3PnLmiPOLQbyviO.g2av4I.wH1oPM3F5WDxX7DJof9JYwI.', 1, 1, 'Админбек ', 'Админбеков', 'Админулы', 'beka20010829@gmail.com');
INSERT INTO users (id, created_at, deleted_at, updated_at, login, password, department_id, group_id, firstname, lastname, middle_name, email)
VALUES (nextval('s_users'), '2020-01-21 02:54:39.375', NULL, '2020-01-23 12:16:43.32', 'ilyas', '$2a$10$rr1jMMjCS5/EQrGnPviE3u0L6I6QeS7bKL3XoiK1jqsRb3Mt3wGT.', 3, 8, 'Ильяс', 'Жуанышев', 'Исполнитель МИД РК', '');
INSERT INTO users (id, created_at, deleted_at, updated_at, login, password, department_id, group_id, firstname, lastname, middle_name, email)
VALUES (nextval('s_users'), '2020-01-21 03:33:01.647', NULL, NULL, 'admin_mid_europa', '$2a$10$X93bDm6z3AV5NcfA/TTatu5fVG5bmr0hpCHkoLg/5TSQRZyiKn3DK', 3, 2, 'Админ', 'МИД', 'Европа', '');
INSERT INTO users (id, created_at, deleted_at, updated_at, login, password, department_id, group_id, firstname, lastname, middle_name, email)
VALUES (nextval('s_users'), '2020-01-21 03:51:12.848', NULL, '2020-01-21 16:32:56.037', 'yelzhan', '$2a$10$7Nu54iTW7I3f7DLE548OtOnP0ujWrtIk6vGYm4iiOiEw4Y5f71Mh.', 3, 10, 'Елжан', 'Руководитель', 'МИД РК', '');
INSERT INTO users (id, created_at, deleted_at, updated_at, login, password, department_id, group_id, firstname, lastname, middle_name, email)
VALUES (nextval('s_users'), '2020-01-21 18:15:23.644', NULL, NULL, 'rayimbek', '$2a$10$RffSlOLrKwlTW.xe2RT8oe/vB8BdrmENBNx/3tTgFzaNynIFcEgR6', 3, 13, 'Райымбек', 'Таир', 'Ответ Секретарь МИД РК', '');
INSERT INTO users (id, created_at, deleted_at, updated_at, login, password, department_id, group_id, firstname, lastname, middle_name, email)
VALUES (nextval('s_users'), '2020-01-22 05:33:49.818', NULL, '2020-01-22 09:41:38.798', 'yermek', '$2a$10$YOe8cRmzsg6NIYUa4pD1ae6mFrnrOICnhgEd.Lvbx2yD0/A0Om4Wa', 6, 4, 'Ермек','Руководитель ГО','Руководитель ГО', '');
INSERT INTO users (id, created_at, deleted_at, updated_at, login, password, department_id, group_id, firstname, lastname, middle_name, email)
VALUES (nextval('s_users'), '2020-01-22 05:36:13.845', NULL, NULL, 'aibek', '$2a$10$s8OcfB0FWrdj1q8u8kdxN.gOcEvpUqQE8yNSIReIFYju7pD7U70cy', 6, 5, 'Айбек','Багит','Ответ Секретарь ГО', '');
INSERT INTO users (id, created_at, deleted_at, updated_at, login, password, department_id, group_id, firstname, lastname, middle_name, email)
VALUES (nextval('s_users'), '2020-01-22 09:42:33.221', NULL, NULL, 'admin_go_nauka', '$2a$10$EQPOzT4l4aKyEp.bKYxOvetbZpvPcMJd27Rqvw8KjQ83PXIIHsFkK', 6, 2, 'Админ','Наука','ГО', '');
INSERT INTO users (id, created_at, deleted_at, updated_at, login, password, department_id, group_id, firstname, lastname, middle_name, email)
VALUES (nextval('s_users'), '2020-01-22 10:21:38.772', NULL, NULL, 'shyngys', '$2a$10$skif8pxKbJTw3rRVLeAERec8Nkkqc5hMGnqQ1rEe2065CpVWWsIae', 6, 3, 'Шынгыс','Заявитель','ГО', '');

INSERT INTO users (id, created_at, deleted_at, updated_at, login, password, department_id, group_id, firstname, lastname, middle_name, email)
VALUES (nextval('s_users'), '2020-01-22 10:21:38.772', NULL, NULL, 'bekdaulet', '$2a$10$xVsbPEmDODbGTsa49Gv5FOiTV15LNr/fqgF1YKWK9341F1cPdag7u', 6, 7, 'Бекдаулет','Ушкемпиров','Руководитель политического дела', '');
INSERT INTO users (id, created_at, deleted_at, updated_at, login, password, department_id, group_id, firstname, lastname, middle_name, email)
VALUES (nextval('s_users'), '2020-01-22 10:21:38.772', NULL, NULL, 'adil', '$2a$10$xVsbPEmDODbGTsa49Gv5FOiTV15LNr/fqgF1YKWK9341F1cPdag7u', 6, 6, 'Adil','Tyulyutaev','Исполнитель политического дела', '');
INSERT INTO users (id, created_at, deleted_at, updated_at, login, password, department_id, group_id, firstname, lastname, middle_name, email)
VALUES (nextval('s_users'), '2020-01-22 10:21:38.772', NULL, NULL, 'nurbergen', '$2a$10$xVsbPEmDODbGTsa49Gv5FOiTV15LNr/fqgF1YKWK9341F1cPdag7u', 8, 6, 'Nurbergen','Khinatolla','Исполнитель политического дела', '');
INSERT INTO users (id, created_at, deleted_at, updated_at, login, password, department_id, group_id, firstname, lastname, middle_name, email)
VALUES (nextval('s_users'), '2020-01-22 10:21:38.772', NULL, NULL, 'biba', '$2a$10$xVsbPEmDODbGTsa49Gv5FOiTV15LNr/fqgF1YKWK9341F1cPdag7u', 6, 6, 'Biba','Moldabekov','Исполнитель политического дела', '');
INSERT INTO users (id, created_at, deleted_at, updated_at, login, password, department_id, group_id, firstname, lastname, middle_name, email)
VALUES (nextval('s_users'), '2020-01-22 10:21:38.772', NULL, NULL, 'islam', '$2a$10$xVsbPEmDODbGTsa49Gv5FOiTV15LNr/fqgF1YKWK9341F1cPdag7u', 6, 12, 'Islam','Amangeldi','Руководитель ДКС', '');
INSERT INTO users (id, created_at, deleted_at, updated_at, login, password, department_id, group_id, firstname, lastname, middle_name, email)
VALUES (nextval('s_users'), '2020-01-22 10:21:38.772', NULL, NULL, 'sanzhar', '$2a$10$xVsbPEmDODbGTsa49Gv5FOiTV15LNr/fqgF1YKWK9341F1cPdag7u', 6, 11, 'Sanzhar','Ratbek','Исполнитель ДКС', '');
INSERT INTO users (id, created_at, deleted_at, updated_at, login, password, department_id, group_id, firstname, lastname, middle_name, email)
VALUES (nextval('s_users'), '2020-01-22 10:21:38.772', NULL, NULL, 'dina', '$2a$10$xVsbPEmDODbGTsa49Gv5FOiTV15LNr/fqgF1YKWK9341F1cPdag7u', 6, 14, 'Dina','Serikova','Исполнитель ВФД', '');
INSERT INTO users (id, created_at, deleted_at, updated_at, login, password, department_id, group_id, firstname, lastname, middle_name, email)
VALUES (nextval('s_users'), '2020-01-22 10:21:38.772', NULL, NULL, 'aboka', '$2a$10$xVsbPEmDODbGTsa49Gv5FOiTV15LNr/fqgF1YKWK9341F1cPdag7u', 6, 10, 'Aboka','Vahid','Руководитель ВФД', '');


INSERT into currencies(id, created_at, deleted_at, updated_at, name, symbol, tenge_buy_ratio, tenge_sell_ratio)
VALUES (nextval('s_currencies'), now(), null, null, 'Тенге', 'KZT', 1, 1);

INSERT into currencies(id, created_at, deleted_at, updated_at, name, symbol, tenge_buy_ratio, tenge_sell_ratio)
VALUES (nextval('s_currencies'), now(), null, null, 'Доллар США', 'USD', 379.5, 380.5);

INSERT into currencies(id, created_at, deleted_at, updated_at, name, symbol, tenge_buy_ratio, tenge_sell_ratio)
VALUES (nextval('s_currencies'), now(), null, null, 'Евро', 'EUR', 415, 417);

INSERT into messages(id, created_at, deleted_at, updated_at, email, message)
VALUES (nextval('s_messages'), now(), null, null, 'Nur@gmail.com', '90909000');

INSERT into business_trip_routes(id, created_at, deleted_at, updated_at, route_name)
VALUES (nextval('s_business_trip_routes'), now(), null, null, 'Алматы - Пекин');

INSERT into business_trip_routes(id, created_at, deleted_at, updated_at, route_name)
VALUES (nextval('s_business_trip_routes'), now(), null, null, 'Рим - Москва');

INSERT into business_trip_routes(id, created_at, deleted_at, updated_at, route_name)
VALUES (nextval('s_business_trip_routes'), now(), null, null, 'Москва - Лондон');

INSERT into business_trip_cities(id, created_at, deleted_at, updated_at, city_name)
VALUES (nextval('s_business_trip_cities'), now(), null, null, 'Лондон');

INSERT into business_trip_cities(id, created_at, deleted_at, updated_at, city_name)
VALUES (nextval('s_business_trip_cities'), now(), null, null, 'Париж');

INSERT into business_trip_cities(id, created_at, deleted_at, updated_at, city_name)
VALUES (nextval('s_business_trip_cities'), now(), null, null, 'Шанхай');

INSERT into business_trip_cities(id, created_at, deleted_at, updated_at, city_name)
VALUES (nextval('s_business_trip_cities'), now(), null, null, 'Пекин');

INSERT into business_trip_cities(id, created_at, deleted_at, updated_at, city_name)
VALUES (nextval('s_business_trip_cities'), now(), null, null, 'Токио');

INSERT into business_trip_cities(id, created_at, deleted_at, updated_at, city_name)
VALUES (nextval('s_business_trip_cities'), now(), null, null, 'Мадрид');