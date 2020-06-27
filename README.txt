===================================================
		        PROGETTO MOVIDA
===================================================
Corso di "Algoritmi e Strutture Dati"
Università di Bologna 
A.A. 2019/2020

Realizzato dal gruppo: '); DROP TABLE Gruppi;--
- Edoardo Merli matricola 0000916655
- Samuele Marro matricola 0000921930

Algoritmi e strutture dati assegnati:
- Algoritmi di ordinamento: BubbleSort, QuickSort
- Implementazioni dizionario: ArrayOrdinato, ABR

---------------------------------------------------
	      Istruzioni per compilare/eseguire
---------------------------------------------------

Aggiungere i contenuti della cartella
src/movida/ e importare la classe MovidaCore.
Creare un'istanza con new MovidaCore() e
caricare un file di database con
MovidaCore.loadFromFile.
Se si desidera, eseguire i test della cartella
test/movida/marromerli/.

---------------------------------------------------
		           Funzionamento
---------------------------------------------------

La classe MovidaCore è la classe entry-point dell'
applicazione MOVIDA, che permette di interagire con 
una knowledge-base a tema cinema importando film,
visualizzando informazioni su di essi e sugli
attori e cercando informazioni in base a diversi
criteri.

MovidaCore mantiene le informazioni sui film e gli
attori e direttori che ne hanno preso parte in vari
strutture di dati:
- 2 liste di film, ordinate in base a 2 criteri
  differenti
- 2 liste di persone, una composta da attori e una 
  contenente anche coloro che hanno svolto come 
  unico ruolo quello di direttori
- 5 dizionari, che associano a vari campi, una 
  serie di film o di persone
- 1 grafo, che rappresenta le relazioni di 
  collaborazione (attori che hanno partecipato
  agli stessi film)

Questa scelta di mantenere i dati in varie strutture
è stata motivata dalla volontà di sacrificare spazio
in memoria e tempo nel caricamento dei film per
ottenere tempi di risposta molto migliori nelle 
varie query.

Le classi BubbleSort e QuickSort implementano i
corrispondenti algoritmi di ordinamento assegnati,
mentre ABR e SortedArrayDictionary implementano i
dizionari.
I primi due implementano l'interfaccia Sorter, 
mentre gli ultimi quella Dictionary.

La classe CaseInsensitiveString è stata necessaria
per identificare un film col suo titolo o una 
persona con il proprio nome in maniera case-insensitive.

La classe CollaborationGraph è stata realizzata al
fine di rappresentare le collaborazioni fra attori
e rispondere alle relative query. Il grafo mantenuto
in MovidaCore è appunto un'istanza di CollaborationGraph

La classe VariablePriorityQueue è stata necessaria per
avere a disposizione una coda con priorità con un 
comportamento più simile a quello studiato a lezione 
rispetto a quella fornita da java.util, e viene 
utilizzata nella risposta a maximiseCollaborations per
implementare l'algoritmo di Prim.