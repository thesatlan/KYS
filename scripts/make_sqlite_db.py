# -*- coding: utf-8 -*-

import sqlite3
import argparse
import os
import struct

IDENTIFIERS_SPLITTER    = "."
JPEG_EXT                = ".jpg"

SQL_CREATE_PEOPLE_TABLE = """ CREATE TABLE IF NOT EXISTS people (
                                        id integer PRIMARY KEY,
                                        name VARCHAR(42) NOT NULL,
                                        pictures blob NOT NULL
                                    ); """

PROGRAM_DESCRIPTION = """ This program creates an sqlite3 db from '.jpg' files in a
directory, it creates a table called "people" with the columns: 'id', 'name', 'pictures' """


def dir(path):
    if os.path.isdir(path):
        return path
    raise ValueError("not a directory")


def create_connection(db_file):
    """ create a database connection to the SQLite database
        specified by db_file
    :param db_file: database file
    :return: Connection object or None
    """
    try:
        conn = sqlite3.connect(db_file)
        return conn
    except Error as e:
        print(e)
 
    return None

def create_table(conn):
    """ create a table from the create_table_sql statement
    :param conn: Connection object
    :param create_table_sql: a CREATE TABLE statement
    :return:
    """
    try:
        c = conn.cursor()
        c.execute(SQL_CREATE_PEOPLE_TABLE)
    except Exception, ex:
        print(e)

class PersonProfile:
    def __init__(self, number, name, pictures_paths):
        # If no ID was found, 
        if number == "":
            self.number = "NULL"
        else:
            self.number = number

        self.name = name
        self.pictures_blob = ""

        for picture_path in pictures_paths:
            with open(picture_path, "rb") as picture:
                data = picture.read()
                picture_blob = struct.pack(">L", len(data)) + data
                self.pictures_blob += picture_blob


def create_persons_profiles(directory):
    directory = unicode(directory)

    persons_dirs_names = [dir_name for dir_name in os.listdir(directory) 
                                if os.path.isdir(os.path.join(directory, dir_name))]
    viewed_identifiers = persons_dirs_names[0].count(IDENTIFIERS_SPLITTER) + 1

    persons_list = []
    for person_dir_name in persons_dirs_names:
        # Getting the identifiers from the person's dir name.
        identifers = person_dir_name.split(IDENTIFIERS_SPLITTER)
        if len(identifers) != viewed_identifiers:
            raise Exception("Number of identifiers is not constant across all persons.")
        elif len(identifers) == 1:
            person_number = ""
            person_name = identifers[0]
        else:
            person_number = identifers[0]
            person_name = identifers[1]

        person_dir = os.path.join(directory, person_dir_name)

        pictures_paths = [os.path.join(person_dir, pic_name) for pic_name in os.listdir(person_dir) 
                                                            if os.path.splitext(pic_name)[1] == JPEG_EXT]

        persons_list.append(PersonProfile(person_number, person_name, pictures_paths))

    return persons_list


def add_person_to_db(conn, person):
    try:
        c = conn.cursor()
        c.execute("INSERT INTO PEOPLE VALUES({}, :name, :pictures)".format(person.number), 
            dict(name=person.name, pictures=sqlite3.Binary(person.pictures_blob)))
        return c.lastrowid
    except Exception, ex:
        print(ex)


def main():
    parser = argparse.ArgumentParser(description=PROGRAM_DESCRIPTION)
    parser.add_argument(
        '-o', '--output', 
        help="output path for the db, default is 'pictures.db' in the 'assets/databases' folder.", 
        type=str, 
        default="../app/src/main/assets/databases/pictures.db")
    parser.add_argument('-d', '--directory',
                        help="a directory to collect '.jpg' files from", type=dir, required=True)

    args = parser.parse_args()
    
    if os.path.exists(args.output):
        os.remove(args.output)
    conn = create_connection(args.output)
    create_table(conn)
    for person_profile in create_persons_profiles(args.directory):
        add_person_to_db(conn, person_profile)
    conn.commit()

if __name__ == '__main__':
    main()
