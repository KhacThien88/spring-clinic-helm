{{- define "common.deployment" -}}
apiVersion: apps/v1
kind: Deployment
metadata:
  name: "{{ .Values.appName }}-deployment"
  namespace: {{ .Release.Namespace }}
  labels:
    app: {{ .Values.appName }}
spec:
  replicas: {{ .Values.replicaCount | default 1 }}
  revisionHistoryLimit: {{ .Values.revisionHistoryLimit | default 10 }}
  selector:
    matchLabels:
      app: {{ .Values.appName }}
  template:
    metadata:
      labels:
        app: {{ .Values.appName }}
        env: {{ .Values.env }}
    spec:
      tolerations: {{ $.Values.global.tolerations | toYaml | nindent 8 }}
      affinity:
        podAntiAffinity:
          preferredDuringSchedulingIgnoredDuringExecution:
            - weight: 100
              podAffinityTerm:
                labelSelector:
                  matchExpressions:
                  - key: env
                    operator: In
                    values: [{{ .Values.env }}] 
                topologyKey: kubernetes.io/hostname
      dnsPolicy: ClusterFirst
      restartPolicy: Always
      imagePullSecrets:
        - name: {{ .Values.image.pullSecret }}
{{- if .Values.initContainers }}
      initContainers:
{{ toYaml .Values.initContainers | indent 8 }}
{{- end }}
      containers:
        - name: {{ .Values.appName }}
          image: {{ .Values.image.repository }}:{{ .Values.image.tag | default "latest" }}
          imagePullPolicy: {{ .Values.image.pullPolicy | default "IfNotPresent" }}
          ports:
            - containerPort: {{ .Values.service.port }}
              name: {{ .Values.service.name | default "tcp" }}
              protocol: TCP
          volumeMounts:
            - name: config-volume
              mountPath: /app/config/application.properties
              subPath: application.properties
      volumes:
        - name: config-volume
          configMap:
            name: {{ .Values.configMap.name }}
            defaultMode: 420
{{- end }}
