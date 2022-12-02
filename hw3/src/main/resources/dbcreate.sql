drop table if exists aircrafts cascade;
create table aircrafts
(
    aircraft_code char(3) not null primary key,
    model         jsonb   not null,
    range         int     not null
);

drop table if exists airports cascade;
create table airports
(
    airport_code char(3) not null primary key,
    airport_name jsonb   not null,
    city         jsonb   not null,
    coordinates  point   not null,
    timezone     text    not null
);

drop table if exists flights cascade;
create table flights
(
    flight_id           serial      not null primary key,
    flight_no           char(6)     not null,
    scheduled_departure timestamp   not null,
    scheduled_arrival   timestamp   not null,
    departure_airport   char(3)     not null,
    arrival_airport     char(3)     not null,
    status              varchar(20) not null,
    aircraft_code       char(3)     not null,
    actual_departure    timestamp,
    actual_arrival      timestamp
);

drop table if exists tickets cascade;
create table tickets
(
    ticket_no      char(13)    not null primary key,
    book_ref       char(6)     not null,
    passenger_id   varchar(20) not null,
    passenger_name text        not null,
    contact_data   jsonb
);

drop table if exists ticket_flights cascade;
create table ticket_flights
(
    ticket_no       char(13)       not null references tickets (ticket_no) on delete cascade,
    flight_id       int            not null references flights (flight_id),
    fare_conditions varchar(10)    not null,
    amount          numeric(10, 2) not null,
    constraint ticket_flight_relation_pk primary key (ticket_no, flight_id)
);

drop table if exists boarding_passes cascade;
create table boarding_passes
(
--     id          int        not null primary key,
    ticket_no   char(13)   not null references tickets (ticket_no) on delete cascade,
    flight_id   int        not null references flights (flight_id),
    boarding_no int        not null,
    seat_no     varchar(4) not null,
    constraint ticket_flight_boarding_pk primary key (ticket_no, flight_id)
);

drop table if exists bookings cascade;
create table bookings
(
    book_ref     char(6)        not null primary key,
    book_date    timestamp      not null,
    total_amount numeric(10, 2) not null
);

drop table if exists seats cascade;
create table seats
(
    aircraft_code   char(3)     not null references aircrafts(aircraft_code),
    seat_no         varchar(4)  not null,
    fare_conditions varchar(10) not null,
    constraint seat_aircraft_pk primary key (seat_no, aircraft_code)
);




