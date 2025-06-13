dev:
	kubectl apply -f argocd/project.yaml
	kubectl apply -f argocd/dev/
staging:
	kubectl apply -f argocd/project-staging.yaml
	kubectl apply -f argocd/staging/