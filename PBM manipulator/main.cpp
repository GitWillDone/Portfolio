#include <iostream>
#include <cstdint>
#include <optional>
#include <tuple>
#include <fstream>
#include <sys/stat.h>
#include <vector>
#include <map>
#include <string>
//#include "huff-v1.h"
#include "littleHuff.h"
using namespace std;

void printBytes(const vector<char> &vector){
    for(char c: vector){
        cout << (int)c;
    }
    cout << endl;
}

// NB this only compiles using -std=c++17 or later
// if anything goes wrong, these functions should print an explanation and then return either false or an empty option type
optional<tuple<unsigned, unsigned, vector<uint16_t> *>>readPBM(const string FileName) {
    ifstream myFile(FileName, ios::in | ios::binary);

    string magicNumber;
    myFile >> magicNumber;
    if(magicNumber != "P4"){
        perror("readBMP() error: invalid input file");
    }

    string widthString;
    string heightString;
    myFile >> widthString;
    myFile >> heightString;
    unsigned width = stoi(widthString);
    unsigned height = stoi(heightString);

    //get rest of file
    vector<char> fileBytes;
    char c;
    while (myFile.get(c))
        fileBytes.push_back(c);
    myFile.close();
}

bool compress(const vector<uint16_t> &Data, const unsigned Width, const unsigned Height, const string FileName){
    return false;
}

//todo
optional<tuple<unsigned, unsigned, vector<uint16_t> *>>readCompressed(const string FileName){
    vector<uint16_t> results;

    //open an input stream to read the magic number
    ifstream myFile(FileName, ios::in | ios::binary);
    if (!myFile.is_open()){ //myFile didn't open correctly
        perror("File failed to open.\n");
        exit(1);
    }

    //read the magic number
    char magicNumber[4];
    char width[4];
    char height[4];

    myFile.read(magicNumber, 4);
    myFile.read(width, 4);
    myFile.read(height, 4);

    //PBMC must be magic number or the input file is bad
    assert(magicNumber[0] == 'P' && magicNumber[1] == 'B' && magicNumber[2] == 'M' && magicNumber[3] == 'C');

    //write the header to the output file
    ofstream out("/Users/willdunn/GitWillDone/cs6015/assignment9/output.bin", ios::binary); //todo should this be bin or .txt?
    if(!out.is_open()){
        perror("There was an error creating the output file.\n");
        exit(1);
    }

    uint32_t magicNum = *reinterpret_cast<int *>(magicNumber);
    uint32_t decompressedWidth = *reinterpret_cast<int *>(width);
    uint32_t decompressedHeight = *reinterpret_cast<int *>(height);
    std::cout << magicNum << " " << decompressedWidth << " " << decompressedHeight << "\n";

    //write to file in the specified order netpbm with a comment that states the filename
    out << "P4\n# " << FileName << "Output\n" << decompressedWidth << " " << decompressedHeight << "\n";

    //build a hashmap of the huffman table in the file; key: number (100101) value: index (frequency) - todo recall these will be in 16 bit blocks
    std::map<string,int> mappy;
    //create the hashmap from the huffman table
    for(int i = 0; i < 3; i++){ //todo change the size to 65536 when comlete
        mappy[littleHuff[i]] = i; //todo this needs to be huff when complete
    }

    for( auto const& [key, val] : mappy ) //todo delete when finished.  this is for visualization
    {
        std::cout << key         // string (key)
                  << ':'
                  << val        // string's value
                  << endl ;
    }

    //decompress using the hashmap


    //add in the header information; will have to have the magic number


    //create the file from the decompression - ensuring that the array is in 16-bit sections
    results.push_back(67);

    out.close();
    myFile.close();

    tuple tup = make_tuple((unsigned) decompressedWidth, (unsigned) decompressedHeight, &results);
    auto opt = make_optional<tuple<unsigned, unsigned, vector<uint16_t> *>> (tup);
    auto var = opt.value();

    return opt;
}

bool writePBM(const vector<uint16_t> &Data, const unsigned Width, const unsigned Height, const string FileName){
    return false;
}

int main() {
//    ofstream os("/Users/willdunn/GitWillDone/cs6015/assignment9/test.bin");
//
//    os<<"P"<<"B"<<"M"<<"C";
//
//    int width = 500;
//    int height = 500;
//    os.write(reinterpret_cast<char *>(&width), 4);
//    os.write(reinterpret_cast<char *>(&height), 4);


    readCompressed("/Users/willdunn/GitWillDone/cs6015/assignment9/test.bin");
    //readPBM("/Users/btrueman/pbmcompress-s19-group1/pbm/x-0000.pbm"); //todo this needs to be your file

}