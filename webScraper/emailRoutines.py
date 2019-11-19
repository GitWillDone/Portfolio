import smtplib
from email.mime.multipart import MIMEMultipart
from email.mime.text import MIMEText
from bs4 import BeautifulSoup
import requests
import sqlite3

# get the email address to send to and from using a local file storing this, which is in cipher text
def getEmailAddress():
    filename = "/Users/willdunn/GitWillDone/cs6015/crdntls.txt"
    decipher = ""
    with open(filename, mode='r', encoding='utf-8') as contacts_file:
        usr = contacts_file.readline()
        for letter in usr:
            if (letter == '\n'):
                break
            decipher += chr(ord(letter) + 7)
            contacts_file.close()
        return decipher

def getPassword():
    filename = "/Users/willdunn/GitWillDone/cs6015/crdntls.txt"
    decipher = ""
    with open(filename, mode='r', encoding='utf-8') as contacts_file:
        # advance to the second line
        pss = contacts_file.readline()
        pss = contacts_file.readline()
        decipher = ""
        for digit in pss:
            if (digit == '\n'):
                break
            decipher += chr(ord(digit) + 13)
        contacts_file.close()
        return decipher

def sendEmail(address, password, msg):
    try:
        server = smtplib.SMTP('smtp.mail.yahoo.com', 587)  # 465 is the ssl alternative, but 578 requires tls
        server.ehlo()
        server.starttls()
        server.ehlo()
        server.login(address, password)
        message = MIMEMultipart()
        message['From'] = address
        message['To'] = address
        message['Subject'] = 'Deck Updates'
        message.attach(MIMEText(msg, 'plain'))
        text = message.as_string()
        server.sendmail(address, address, text)
        server.quit()
    except:
        print("Can\'t connect to send the email\n")

# def emailUpdates(webscrapedDecks, linkNames):
def emailUpdates(deck, linkName):
    connection = sqlite3.connect('decklists.db')
    curs = connection.cursor()
    magicDeck = []
    newRequest = requests.get(linkName)
    newSoup = BeautifulSoup(newRequest.text, 'html.parser')
    magicDeck = newSoup.find_all(class_='cn')

    # the from/to address are identical and grabbed from a remote file to protect privacy
    msg = F"Subject: Decklist Update(s)\n {deck}\n"
    for elem in magicDeck:
        msg += elem.get_text() + "\n"

    address = getEmailAddress() + "@yahoo.com"
    password = getPassword()
    sendEmail(address, password, msg)
