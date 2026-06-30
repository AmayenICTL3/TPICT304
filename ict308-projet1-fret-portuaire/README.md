# ICT308 - Projet 1 : Gestion du Fret Portuaire

Application Java Swing pour le suivi des conteneurs du Port Autonome de Douala.

## Fonctionnalites

- modelisation POO avec `Marchandise`, `ConteneurStandard` et `ConteneurRefrigere`
- polymorphisme pour le calcul des taxes portuaires
- stockage centralise dans `ArrayList<Marchandise>`
- sauvegarde et chargement par serialisation dans `data/fret.ser`
- interface Swing avec `JTable`
- filtrage des conteneurs refrigeres en alerte
- thread de simulation qui modifie les temperatures toutes les 3 secondes

## Compiler

```bash
cd ict308-projet1-fret-portuaire
make compile
```

## Lancer l'application

```bash
make run
```

## Nettoyer

```bash
make clean
```

## Structure

- `src/main/java/.../model` : classes metier
- `src/main/java/.../service` : gestion du stock, persistance, simulation
- `src/main/java/.../ui` : interface Swing
