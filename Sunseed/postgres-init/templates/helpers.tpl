{{- define "postgres-init.name" -}}
{{- .Chart.Name -}}
{{- end }}

{{- define "postgres-init.fullname" -}}
{{- .Release.Name }}-{{ .Chart.Name }}
{{- end }}
