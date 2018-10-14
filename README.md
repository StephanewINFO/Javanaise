# Javanaise

## Objectifs

L'objectif du projet Javanaise est de réaliser en Java un cache d'objets répartis. Les applications Java utilisant Javanaise peuvent créer et accéder à des objets répartis cachés localement. 
On a utilisé Java RMI pour implementer les méthodes de connection entre les Serveurs Locaux et le Coordinateur

## Compiler

Terminal 1
Compilation des fichiers .java dans le dossier avec les sources (javac *.java)
>/src$  javac irc/*.java
>/src$  javac jvn/*.java
rmiregistry

## Exécution 

1. Sur le terminal

Terminal 2 -> Coordinateur
1.1 Exécution de la classe "JvnCoordImpl" (java JvnCoordImpl)


Terminal N (nb de Serveurs Locaux souhaités)
1.2 Exécution de la classe "Irc" (java Irc)
1.3 Répéter pour le nombre de serveux locaux souhaités
1.4 Se laisser guider par la GUI
1.5 Se laisser guider par les commandes dans chaque serveur local pour synchronizer le déverrouillage d'ecriture et lecture entre eux


2. Sur un IDE
2.1 Exécution de la classe "JvnCoordImpl"
2.2 Exécution de la classe  "Irc" (plusieurs exécutions de cette classe cree des serveurs locaux)
2.3 Se laisser guider par la GUI
2.4 Se laisser guider par les commandes dans chaque serveur local pour synchronizer le déverrouillage d'ecriture et lecture entre eux






## Auteurs

* **Stephanie Barona Andrade**
* **Dumitru Corini**
