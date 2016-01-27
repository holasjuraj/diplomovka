Test 1 - Text Diff

Implementovany algoritmus z "An O(ND) Difference Algorithm and Its Variations (Eugene W. Myers)" na text diff.
Porovnavanie efektivity pri diffovani po znakoch, po riadkoch a po tokenoch.
Zaciatok implementacie algoritmu z "An Extension of Ukkonen’s Enhanced Dynamic Programming ASM Algorithm
(Hal Berghel, David Roach)". Ukazalo sa ze je nepouzitelny pre obrovske pamatove naroky.

Vysledky:
Myersov alg. pri porovnavani po znakoch je dobry, ale strasne pomaly (az 1min pri rozdielnych 100kb suboroch)
Pri porovnavani po riadkoch dava stabilne vysledky cca 1.23-krat vyssie, pre porovnavanie to ale nevadi (lebo
je to pomerne stabilny faktor), a je 1290x rychlejsi.

Velky test (kompletne porovnanie 55 suborov) ukazal, ze vacsia odchylka od cca 1.23-nasobneho faktora je iba
pri podobnych suboroch, teda tych kde perL < 15% . Pri nich je ale speedup pomerne maly (priemer 86x,
median 1x).

Novy postup pre kombinaciu presnosti a efektivity:
1) porovnam subory po riadkoch
2) ak je precentualny vysledok < 15% tak porovnam po znakoch, a prenasobim faktorom 1.23
Priemerny speedup tejto metody je 927x

Porovnavanie po tokenoch je z logickeho hladiska najpresnejsie, no neda sa dobre porovnat s vysledkami
predchadzajucich. Casovo je na tom niekde medzi porovnavanim po znakoch a po riadkoch, 61x rychlejsie ako
znakove porovnavanie.
Tokeny:
-> zaciatok elementu bez atributov
-> dvojica atribut="hodnota"
-> koniec elementu
-> riadok textu vnutri elementu (nie cely text, kazdy riadok je jeden token!)

Projekt obsahuje aj vypisanie do Multi-Dimensional Scaling (MDS), a test rychlosti nacitavania suborov
niekolkymi metodami.