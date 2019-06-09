#!/usr/bin/env bash

transaction_files_regex="../health-monitoring/import/data/bitcoin-csv-block-*/sample-transaction-data-*.csv"
transaction_files_all=""

for file in $transaction_files_regex; do
    transaction_files_all=("${transaction_files_all},${file}")
done

transaction_files_all=${transaction_files_all:1:${#transaction_files_all}-1}

echo "Relation input files are ${transaction_files_all}"


echo `curl http://localhost:8090/admin/clusterByInput?data=$transaction_files_all`