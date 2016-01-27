Test 3 - Early stopping v Mayersovom porovnavaciom algoritme
(An O(ND) Difference Algorithm and Its Variations (Eugene W. Myers))

Obohatenie Mayersovho porovnavacieho algoritmu o early stopping:
akonahle pocas behu algoritmu rozdielnost suborov (d) prekroci danu hranicu (EARLY_STOPPING_THRESHOLD),
algoritmus sa zastavi a realny vysledok odhadne z doposial vyratanych udajov. Dobry odhad je gro prace.

--------------------------------------------------------------------------------------------------------------

Princip odhadu:
algoritmus hlada najdalej siahajuce D-cesty od [0,0] smerom ku [n,m], t.j. take ktore pouziju d nediagonalnych
hran. My obmedzime zvacsovanie d na hranicu "max". Ak d presiahne max, tak odchytime bod kde skoncila dana
najdalej siahajuca D-cesta.

[0,0]            n
    +------------------------+
    |\                       |
    | \---\                  |
    |      \---\             |
    |           \            |
  m |           |            |
    |           \---\        |
    |                \---\   |
    |                     *  |
    |          koniec=[x,y]  |
    |                        |
    +------------------------+
                             [n,m]

X-ove pozicie koncov najdalej siahajucich D-ciest na jednotlivych diagonalach su ulozene v poli v.
Z pozicii [x,y] nasledne vypocitame "skore" = ako blizko sa ku cielu dostal, ako vzdialenost start-ciel minus
vzdialenost (tam kde skoncil)-ciel:
  skore = || [0,0] - [n,m] ||  -  || [x,y] - [n,m] ||
Skore sa pocita bud podla L1 alebo L2 normy (Manhattanska alebo Euklidovska vzdialenost).


Odhadnuty vysledok sa potom pocita podla vzorca:

                        max.mozne skore             max.mozne skore
  odhad = posledne d * -----------------  =  max * -----------------
                             skore                       skore

Pozn.: maximalne mozne skore je pri L1 a L2 ine cislo!

--------------------------------------------------------------------------------------------------------------

Nakolko odhadnute hodnoty vytvarali v grafe (realne vs. odhad) paraboloidny tvar pod priamkou y=x, vznikol
dalsi pokus: vziat transformaciu, ktora danu parabolu prevedie na priamku y=x, a prehnat nou vsetky body.
Ukazalo sa, ze body nelezali presne na parabole, ale na inej krivke, ale vo vysledku celkom pekne prilahli ku
priamke y=x, t.j. ku presnemu vysledku.

Funkcia paraboly: vrchol v bode [est,est], prechadza cez bod [max,max]

          (x - est)^2
  f(x) = ------------- + est
           max - est

Inverzna funkcia - transformacia z paraboly na priamku y=x:

  f^-1(x) = est +- sqrt( est^2  - est*max + (max-est)*x )

Pozn: pre nasu transformaciu je pouzite + (z toho +- vo vzorci).

--------------------------------------------------------------------------------------------------------------

V kode je aj zarodok tricky metody, ktora robi linearnu kombinaciu L1 a L2 normy, ale je to k nicomu: ked som
to pisal tak som mal este chybu v pocitani L2, z ktorej som vychadzal. (max.mozne skore bolo nastavene na n+m)

--------------------------------------------------------------------------------------------------------------

V balicku mayersESrefactor je vylepsena verzia (hlavne triedy FileComparator):
-> tokenize() je vynate z editDist*(), kazdy subor sa tokenizuje iba raz na zaciatku. Usetrilo to asi polovicu
   casu.
-> kod editDistL*() s obmadzenim sa viac priblizil pseudokodu v clanku
-> ostali iba porovnavacie funkcie:
   -> editDist() - povodny algoritmus bez early stopping
   -> editDistL1() - s early stopping pouzitim L1 normy
   -> editDistL2() - s early stopping pouzitim L2 normy
-> uprava formatovania vystupu

--------------------------------------------------------------------------------------------------------------

Vysledky a zavery:
-> vacsina odhadov je podhodnotena, napr. odhad tvrdi ze su rozdielne na 20%, v skutocnosti su az na 35% ine
-> L2 norma vseobecne dosahuje o nieco lepsie vysledky ako L1. Niekedy menej, niekedy viac, ale prakticky vzdy
   je aspon o nieco presnejsia.
-> transformacie z paraboly na priamku sa nebudu pouzivat. Vysledky maju sice pekne: postupne sa priklonia
   velmi blizko priamke y=x, maju mansiu priemernu chybu, ALE: v odhadoch bezprostredne za EST maju
   niekolkonasobne vacsiu chybu ako bez transformacie. Pre nas program je dolezite, aby prave tieto odhady
   boli presne, radsej nech su vacsie odchylky pri velmi rozdielnych suboroch. T.j. radsej mat 60% miesto 90%
   a 21% miesto 25%, ako mat 78% miesto 80% a 36% miesto 21%.
-> so zvysujucim sa EST sa prudko zhorsuje zrychlenie, preto ho treba zachovavat co najnizsie. Pre korektne
   vysledky by malo platit EST > (HAC max. merging threshold). Mozno budu ale prijatelne dobre vysledky aj ked
   tato nerovnost platit nebude, alebo aspon nebude ostra.
-> stale je to pomale: porovnanie 1000 jobov pouzitim L2 normy pri EST=10% (t.j. tak akurat) na 4 threadoch
   zaberie zhruba hodinu