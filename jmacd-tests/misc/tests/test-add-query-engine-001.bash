#!/bin/bash

set -e
set -E

curl  -H "Content-Type: application/json" -k -X POST https://localhost:8443/add-query-engine --data @'C:\business\saasware\macdservices\jmacd\jmacd-server\misc\test-data\add-query-engine-001.json'
