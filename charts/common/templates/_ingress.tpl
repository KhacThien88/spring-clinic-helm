{{- define "common.ingress" -}}
apiVersion: networking.k8s.io/v1
kind: Ingress
metadata:
    name: "{{ .Values.appName }}-ingress"
    namespace: {{ .Release.Namespace }}
    annotations:
      nginx.ingress.kubernetes.io/backend-protocol: "HTTP"
spec:
  ingressClassName: nginx
  rules:
    - host: {{ .Values.ingress.domainName | quote}}
      http:
        paths:
        - path: {{ .Values.ingress.path }}
          pathType: {{ .Values.ingress.pathType }}
          backend:
            service:
              name: "{{ .Values.appName }}-service"
              port:
                number: {{ .Values.service.port }}
{{- end }}
