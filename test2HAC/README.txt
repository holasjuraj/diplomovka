Test 2 - Hierarchical agglomerative clustering (HAC)

Implementovane hierarchicke aglomerativne (zdola nahor) clusterovanie v case O(n^3).
Porovnavacie funkcie:
  1) UPGMA
  2) C-link
  3) S-link
Stopping condition:
  1) vrati stav pred spojenim s najvacsiou merging distance
  2) vrati stav ked merging distance aktualneho spajania presiahne stanovanu hranicu

Clustre su ulozene ako dendrogram, aby bolo mozna spatna rekonstrukcia procesu.