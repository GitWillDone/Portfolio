import webscraper
import sqlite3

def tests():
    #start by deleting db
    connection = sqlite3.connect('decklists.db')
    curs = connection.cursor()
    curs.execute("DELETE FROM decks")
    curs.execute("SELECT FROM decks")
    delData = curs.fetchone()
    if delData is not None:
        print("Error with db deletion")

    #test for decks and links
    webscraper.webscrape()
    curs.execute("SELECT FROM decks")
    deckData = curs.fetchall()
    if len(deckData) is not 4:
        print("Error with db deck integration")

    #close db connection
    connection.close()