#!/bin/bash
echo -e "Content-Type: application/json\n"

# Control de Idempotencia
CACHE_DIR="/var/tmp"
if [[ -n "$HTTP_IDEMPOTENCY_KEY" ]]; then
    # Sanitizar la llave para evitar path traversal
    SAFE_KEY=$(echo "$HTTP_IDEMPOTENCY_KEY" | tr -cd 'a-zA-Z0-9_-')
    CACHE_FILE="${CACHE_DIR}/calc_idempotency_${SAFE_KEY}.json"
    
    if [[ -f "$CACHE_FILE" ]]; then
        cat "$CACHE_FILE"
        exit 0
    fi
fi

# Parseo de QUERY_STRING (op=sum&a=3&b=4)
saveIFS=$IFS
IFS='&'
for param in $QUERY_STRING; do
    case $param in
        op=*) op="${param#op=}" ;;
        a=*) a="${param#a=}" ;;
        b=*) b="${param#b=}" ;;
    esac
done
IFS=$saveIFS

# Validación de entradas
if [[ -z "$op" || -z "$a" || -z "$b" ]]; then
    echo '{"error": "Faltan parametros (op, a, b)"}'
    exit 1
fi

# Lógica matemática con bc
case $op in
    sum) res=$(echo "$a + $b" | bc -l) ;;
    sub) res=$(echo "$a - $b" | bc -l) ;;
    mul) res=$(echo "$a * $b" | bc -l) ;;
    div)
        if [[ "$b" == "0" ]]; then
            echo '{"error": "Division por cero"}'
            exit 1
        fi
        # scale=4 limita los decimales en la división
        res=$(echo "scale=4; $a / $b" | bc -l)
        ;;
    *)
        echo '{"error": "Operacion invalida"}'
        exit 1
        ;;
esac

# Generar el payload JSON
PAYLOAD=$(cat <<EOF
{
  "operacion": "$op",
  "a": $a,
  "b": $b,
  "resultado": $res,
  "idempotencia": "${HTTP_IDEMPOTENCY_KEY:-ninguna}"
}
EOF
)

# Imprimir salida al cliente y persistir en caché si se envió llave de idempotencia
echo "$PAYLOAD"
if [[ -n "$HTTP_IDEMPOTENCY_KEY" ]]; then
    echo "$PAYLOAD" > "$CACHE_FILE"
fi