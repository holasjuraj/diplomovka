Prototyp

Uceleny program s funkciami:
-> nacita subory v zadanom priecinku
-> porovna ich vybranou metodou:
  -> po znakoch
  -> po riadkoch
  -> po tokenoch
  -> kombinovane po znakoch+riadkoch
-> spusti HAC so zadanou porovnavaciou funkciou:
  -> UPGMA
  -> C-link
  -> S-link
-> mozne stopping conditions pre HAC (urcuje sa podla posledneho parametru HAC.clusterize()) :
  -> vrati stav pred spojenim s najvacsiou merging distance ( = 0)
  -> vrati stav ked merging distance aktualneho spajania presiahne stanovanu hranicu ( > 0)

Vysledky testovania:
-> porovnavanie po znakoch je nepouzitelne pomale
-> porovnavanie iba po riadkoch dava nie dostatocne dobre vysledky
-> vysledky UPGMA/C-link/S-link aj porovnavania po tokenoch/kombinovane su velmi podobne (a to celkom dobre)