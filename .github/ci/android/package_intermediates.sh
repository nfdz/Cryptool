#!/bin/bash -xe

cd androidApp/build/intermediates/merged_native_libs/release/out/lib
zip -r release_native_symbols.zip ./*
cd -
mv androidApp/build/intermediates/merged_native_libs/release/out/lib/release_native_symbols.zip .