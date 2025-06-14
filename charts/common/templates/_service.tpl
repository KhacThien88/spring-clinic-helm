{{- define "common.service" -}}
apiVersion: v1
kind: Service
metadata:
  name: "{{ .Values.appName }}-service"
  namespace: {{ .Release.Namespace }}
  labels:
    app: {{ .Values.appName }}
spec:
  type: {{ .Values.service.type | default "ClusterIP" }}
  selector:
    app: {{ .Values.appName }}
  ports:
    - name: {{ .Values.service.name | default "tcp" }}
      port: {{ .Values.service.port | default 80 }}
      targetPort: {{ .Values.service.targetPort | default .Values.service.port }}
{{- if eq .Values.service.type "NodePort" }}
      nodePort: {{ .Values.service.nodePort | default 30000 }}
{{- end }}
{{- end }}