import sqlite3
import emailRoutines


def createDBIfNonexistent():
    connection = sqlite3.connect('decklists.db')
    curs = connection.cursor()

    curs.execute("""CREATE TABLE IF NOT EXISTS decks  (
        deckName text,
        link text
        )""")
    connection.commit()
    connection.close()


def addDecksToDB(webscrapedDecks, linkNames):
    connection = sqlite3.connect('decklists.db')
    curs = connection.cursor()

    for index, deck in enumerate(webscrapedDecks):
        curs.execute("SELECT deckName FROM decks WHERE deckName = ?", (deck,))
        newDeck = curs.fetchone()
        if newDeck is None:  # deck is not in db
            curs.execute("INSERT INTO decks(deckName, link) VALUES(?,?)", (deck, linkNames[index]))
            emailRoutines.emailUpdates(deck, linkNames[index])

    connection.commit()
    connection.close()


def updateDB(webscrapedDecks):
    connection = sqlite3.connect('decklists.db')
    curs = connection.cursor()
    # find the deck, in the database, which is no longer tier 1 (if any), and remove it
    curs.execute("SELECT deckName FROM decks")
    databaseDecks = curs.fetchall()
    if len(databaseDecks) > 4:  # 4 is the number of decks that will considered for tier 1
        for elem in databaseDecks:
            if elem[0] not in webscrapedDecks:
                curs.execute("DELETE FROM decks WHERE deckName = ?", (elem))  # delete the entire row
    # curs.execute("DELETE FROM decks")
    connection.commit()
    connection.close()
