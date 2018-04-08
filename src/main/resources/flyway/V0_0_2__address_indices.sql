CREATE INDEX from_address_index ON ethereum_transaction (from_address);
CREATE INDEX to_address_index ON ethereum_transaction (to_address);
CREATE INDEX ethereum_contract_index ON ethereum_transaction (ethereum_contract);