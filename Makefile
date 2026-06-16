BASE_URL ?= http://127.0.0.1:8003

.PHONY: start test unit integration coverage

start:
	npm start

test:
	npm test

unit:
	npm run test:unit

integration:
	npm run test:integration

coverage:
	npm run test:coverage
