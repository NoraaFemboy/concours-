# Concours+

Application Android native (Java) — première version de test.

## Fonctions incluses
- Interface sombre adaptée au mobile.
- Recherche par nom, lot ou catégorie.
- Catégories PC gamer, composants, périphériques et autres.
- Ajout manuel de concours avec lien de l'organisateur.
- Favoris enregistrés localement sur le téléphone.
- Fiche détail et ouverture du site officiel.
- Compilation automatique de l'APK debug avec GitHub Actions.

## Important
Cette version ne récupère pas automatiquement les concours sur Internet. Elle ne contient aucun faux concours : ajoute uniquement des concours dont tu as vérifié la source, le règlement et la date. Les données sont stockées localement sur l'appareil.

## Importer dans GitHub depuis un téléphone
1. Télécharge et décompresse `ConcoursPlus.zip`.
2. Ouvre le dépôt GitHub `NoraaFemboy/concours-` dans Chrome.
3. Dans l'onglet **Code**, choisis **Add file** puis **Upload files** (sur mobile, menu `+` ou menu `…` selon l'affichage).
4. Sélectionne tous les fichiers et dossiers du projet décompressé, y compris le dossier caché `.github`.
5. Valide avec **Commit changes** sur la branche `main`.

Si le site mobile ne permet pas d'envoyer le dossier `.github`, utilise l'affichage « Site pour ordinateur » dans Chrome.

## Récupérer l'APK
1. Ouvre l'onglet **Actions** du dépôt.
2. Ouvre le workflow **Build Android APK**.
3. Attends la coche verte.
4. Ouvre l'exécution terminée, descends à **Artifacts** et télécharge `ConcoursPlus-debug-apk`.
5. Décompresse l'archive téléchargée : `app-debug.apk` est l'application à installer.

## Version
0.1.0 — prototype fonctionnel. APK debug non signé pour publication Play Store.
