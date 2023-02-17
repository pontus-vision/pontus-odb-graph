#!/usr/bin/env bash

set -e
DIR="$( cd "$(dirname "$0")" ; pwd -P )"
cd "$DIR"

mkdir -p "${DIR}/rules"

cat $1 | jinja2 --strict \
   -DcurrRule=webiny_ropa \
   -DfileHeaders='DO NOT UPDATE THIS FILE MANUALLY; ANY CHANGES WILL BE OVERRIDDEN BY THE J2 DIR TEMPLATES' \
   -o "${DIR}/rules/webiny_ropa.yaml" templates/rules.yaml.j2

yq . "${DIR}/rules/webiny_ropa.yaml"  >  "${DIR}/rules/webiny_ropa.json"

cat $1 | jinja2 --strict \
   -DcurrRule=webiny_legal_actions \
   -DfileHeaders='DO NOT UPDATE THIS FILE MANUALLY; ANY CHANGES WILL BE OVERRIDDEN BY THE J2 DIR TEMPLATES' \
   -o "${DIR}/rules/webiny_legal_actions.yaml" templates/rules.yaml.j2

yq . "${DIR}/rules/webiny_legal_actions.yaml"  >  "${DIR}/rules/webiny_legal_actions.json"

cat $1 | jinja2 --strict \
   -DcurrRule=webiny_privacy_docs \
   -DfileHeaders='DO NOT UPDATE THIS FILE MANUALLY; ANY CHANGES WILL BE OVERRIDDEN BY THE J2 DIR TEMPLATES' \
   -o "${DIR}/rules/webiny_privacy_docs.yaml" templates/rules.yaml.j2

yq . "${DIR}/rules/webiny_privacy_docs.yaml"  >  "${DIR}/rules/webiny_privacy_docs.json"

cat $1 | jinja2 --strict \
   -DcurrRule=webiny_meetings \
   -DfileHeaders='DO NOT UPDATE THIS FILE MANUALLY; ANY CHANGES WILL BE OVERRIDDEN BY THE J2 DIR TEMPLATES' \
   -o "${DIR}/rules/webiny_meetings.yaml" templates/rules.yaml.j2

yq . "${DIR}/rules/webiny_meetings.yaml"  >  "${DIR}/rules/webiny_meetings.json"

cat $1 | jinja2 --strict \
   -DcurrRule=webiny_privacy_notice \
   -DfileHeaders='DO NOT UPDATE THIS FILE MANUALLY; ANY CHANGES WILL BE OVERRIDDEN BY THE J2 DIR TEMPLATES' \
   -o "${DIR}/rules/webiny_privacy_notice.yaml" templates/rules.yaml.j2

yq . "${DIR}/rules/webiny_privacy_notice.yaml"  >  "${DIR}/rules/webiny_privacy_notice.json"

cat $1 | jinja2 --strict \
   -DcurrRule=webiny_data_source \
   -DfileHeaders='DO NOT UPDATE THIS FILE MANUALLY; ANY CHANGES WILL BE OVERRIDDEN BY THE J2 DIR TEMPLATES' \
   -o "${DIR}/rules/webiny_data_source.yaml" templates/rules.yaml.j2

yq . "${DIR}/rules/webiny_data_source.yaml"  >  "${DIR}/rules/webiny_data_source.json"

cat $1 | jinja2 --strict \
   -DcurrRule=webiny_consents \
   -DfileHeaders='DO NOT UPDATE THIS FILE MANUALLY; ANY CHANGES WILL BE OVERRIDDEN BY THE J2 DIR TEMPLATES' \
   -o "${DIR}/rules/webiny_consents.yaml" templates/rules.yaml.j2

yq . "${DIR}/rules/webiny_consents.yaml"  >  "${DIR}/rules/webiny_consents.json"

cat $1 | jinja2 --strict \
   -DcurrRule=webiny_contracts \
   -DfileHeaders='DO NOT UPDATE THIS FILE MANUALLY; ANY CHANGES WILL BE OVERRIDDEN BY THE J2 DIR TEMPLATES' \
   -o "${DIR}/rules/webiny_contracts.yaml" templates/rules.yaml.j2

yq . "${DIR}/rules/webiny_contracts.yaml"  >  "${DIR}/rules/webiny_contracts.json"

cat $1 | jinja2 --strict \
   -DcurrRule=webiny_organisation \
   -DfileHeaders='DO NOT UPDATE THIS FILE MANUALLY; ANY CHANGES WILL BE OVERRIDDEN BY THE J2 DIR TEMPLATES' \
   -o "${DIR}/rules/webiny_organisation.yaml" templates/rules.yaml.j2

yq . "${DIR}/rules/webiny_organisation.yaml"  >  "${DIR}/rules/webiny_organisation.json"

cat $1 | jinja2 --strict \
   -DcurrRule=webiny_awareness_campaign \
   -DfileHeaders='DO NOT UPDATE THIS FILE MANUALLY; ANY CHANGES WILL BE OVERRIDDEN BY THE J2 DIR TEMPLATES' \
   -o "${DIR}/rules/webiny_awareness_campaign.yaml" templates/rules.yaml.j2

yq . "${DIR}/rules/webiny_awareness_campaign.yaml"  >  "${DIR}/rules/webiny_awareness_campaign.json"

cat $1 | jinja2 --strict \
   -DcurrRule=webiny_owner \
   -DfileHeaders='DO NOT UPDATE THIS FILE MANUALLY; ANY CHANGES WILL BE OVERRIDDEN BY THE J2 DIR TEMPLATES' \
   -o "${DIR}/rules/webiny_owner.yaml" templates/rules.yaml.j2

yq . "${DIR}/rules/webiny_owner.yaml"  >  "${DIR}/rules/webiny_owner.json"

cat $1 | jinja2 --strict \
   -DcurrRule=webiny_dsar \
   -DfileHeaders='DO NOT UPDATE THIS FILE MANUALLY; ANY CHANGES WILL BE OVERRIDDEN BY THE J2 DIR TEMPLATES' \
   -o "${DIR}/rules/webiny_dsar.yaml" templates/rules.yaml.j2

yq . "${DIR}/rules/webiny_dsar.yaml"  >  "${DIR}/rules/webiny_dsar.json"
