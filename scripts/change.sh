#!/bin/bash

DIR="$( cd "$(dirname "$0")" ; pwd -P )"
cd "$DIR"


cat ../conf/reports/Object.Data_Procedures.RIPD.html | \
sed -e 's/Ó/\&Oacute;/g' | \
sed -e 's/Õ/\&Otilde;/g' | \
sed -e 's/Ô/\&Ocirc;/g' | \
sed -e 's/Ò/\&Ograve;/g' | \
sed -e 's/ó/\&oacute;/g' | \
sed -e 's/õ/\&otilde;/g' | \
sed -e 's/ô/\&ocirc;/g' | \
sed -e 's/ò/\&ograve;/g' | \
sed -e 's/é/\&eacute;/g' | \
sed -e 's/ê/\&ecirc;/g' | \
sed -e 's/è/\&egrave;/g' | \
sed -e 's/É/\&Eacute;/g' | \
sed -e 's/Ê/\&Ecirc;/g' | \
sed -e 's/È/\&Egrave;/g' | \
sed -e 's/Á/\&Aacute;/g' | \
sed -e 's/Ã/\&Atilde;/g' | \
sed -e 's/Â/\&Acirc;/g' | \
sed -e 's/À/\&Agrave;/g' | \
sed -e 's/á/\&aacute;/g' | \
sed -e 's/ã/\&atilde;/g' | \
sed -e 's/â/\&acirc;/g' | \
sed -e 's/à/\&agrave;/g' | \
sed -e 's/Ú/\&Uacute;/g' | \
sed -e 's/Ù/\&Ugrave;/g' | \
sed -e 's/Û/\&Ucirc;/g' | \
sed -e 's/ú/\&uacute;/g' | \
sed -e 's/ù/\&ugrave;/g' | \
sed -e 's/û/\&ucirc;/g' | \
sed -e 's/Í/\&Iacute;/g' | \
sed -e 's/Ì/\&Igrave;/g' | \
sed -e 's/í/\&iacute;/g' | \
sed -e 's/ì/\&igrave;/g' | \
sed -e 's/Ç/\&Ccedil;/g' | \
sed -e 's/ç/\&ccedil;/g' | \
sed -e 's/§/\&sect;/g' | \
sed -e 's/º/\&ordm;/g'  > file2.txt
   
   
