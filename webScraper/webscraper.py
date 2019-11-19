from bs4 import BeautifulSoup
import requests
import databaseRoutines



def webscrape():
    # grab the information from the website we'll be scrapping
    request = requests.get('https://mtgadecks.net/')
    soup = BeautifulSoup(request.text, 'html.parser')

    # get the links for all tier 1 decks
    links = soup.find(class_='col-md-3 float-left')

    # grab the first four decks, because that is all that will fit in tier 1
    webscrapedDecks = []
    tierDecks = soup.find_all(class_='tier')
    for index, deck in enumerate(tierDecks):
        if (index < 4):
            newDeck = deck.get_text().replace('\n', '').replace(' ', '')
            webscrapedDecks.append(newDeck)

    linkNames = []
    for link in links.find_all('a', href=True):
        linkNames.append("https://mtgadecks.net" + link['href'])

    # connect to the database. If it doesn't exist, create it
    databaseRoutines.createDBIfNonexistent()

    # add to db any new decks, and send an email with the change(s)
    databaseRoutines.addDecksToDB(webscrapedDecks, linkNames)

    # update the database of any removals
    databaseRoutines.updateDB(webscrapedDecks)

webscrape() #actually run the program