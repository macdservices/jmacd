#!/bin/bash

set -e
set -E

curl  -H "Content-Type: application/json" -k -X POST https://localhost:8443/add-query --data @'C:\business\saasware\macdservices\jmacd\jmacd-server\misc\test-data\add-query-002.json'
