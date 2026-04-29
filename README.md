# API bancaire JavaScript

Version JavaScript simple du projet bancaire, basee sur le module `http` natif de Node.js.

## Lancement

```bash
cd api-javascript
node server.js 8003
```

ou

```bash
cd api-javascript
npm start
```

## Accès

- API : `http://127.0.0.1:8003`
- Swagger : `http://127.0.0.1:8003/docs`

## Endpoints

- `GET /`
- `POST /accounts`
- `GET /accounts`
- `GET /accounts/{account_id}`
- `POST /accounts/{account_id}/deposit`
- `POST /accounts/{account_id}/withdraw`
- `GET /accounts/{account_id}/transactions`

## Limite

Les donnees sont conservees en memoire pendant l'execution du serveur.

## Documents

- cahier de charges : [`CAHIER_DE_CHARGES.pdf`](CAHIER_DE_CHARGES.pdf)
- cas de test : [`CAS_DE_TEST.pdf`](CAS_DE_TEST.pdf)
- rapport d'implementation : [`RAPPORT_IMPLEMENTATION.pdf`](RAPPORT_IMPLEMENTATION.pdf)
- analyse C1/C2 avec complexité cyclomatique et graphes visuels : [`ANALYSE_C1_C2.pdf`](ANALYSE_C1_C2.pdf)
