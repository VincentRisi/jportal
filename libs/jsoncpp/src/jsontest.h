#ifndef jsontestH
#  define jsontestH

#  include "machine.h"
using namespace std;
#  include "json/json.h"
using namespace Json;

class Pinky
{
    int a;
public:
    Pinky() { a = 1; }
    int get_a() { return a; }
};

class Brain : public Pinky
{
public:
    Brain() {}
};

#endif