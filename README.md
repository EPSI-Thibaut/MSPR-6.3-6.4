# MSPR-6.3

Mika LY, Jused ADINSI, Alexia NICOLEAU, Martin KHYARI, Thibaut MOSTEAU

EPSI NANTES
Session 2024-2025

## CERTIFICATION DÉVELOPPEUR EN INTELLIGENCE ARTIFICIELLE ET DATA SCIENCE RNCP 36581

### BLOC DE COMPÉTENCES E6.3 : Produire et maintenir une solution I.A.
### BLOC DE COMPÉTENCES E6.4 : Gérer les activités/tâches du développement d'une solution I.A.

**Cahier des Charges de la MSPR : Mise en production d'une solution à partir des éléments de contextes fournis**

---

## Contexte

Après le développement réussi de l'API IA et de l'application web (MSPR 2), la nouvelle division de l'OMS est prête à mettre en production la plateforme complète. Cette phase vise à assurer la disponibilité, la scalabilité, la sécurité et la maintenabilité de la solution, tout en adoptant les meilleures pratiques DevOps et Agile.

Le projet prend une dimension internationale avec un déploiement dans plusieurs pays :
- **États-Unis** : Gestion de grandes quantités de données
- **France** : Respect strict du RGPD, interface française, pas d'API technique
- **Suisse** : Support multilingue (français, allemand, italien), pas de Dataviz ni d'API technique

---

## Compétences Évaluées

- **Développement back-end** : API et programmes intégrés
- **Développement front-end** : Interface ergonomique et accessible
- **Tests et déploiement** : Plans de tests, déploiement automatisé
- **Supervision** : Monitoring et amélioration continue
- **Gestion de projet Agile** : Méthodologie, communication, reporting
- **Veille technologique** : Système de veille et amélioration continue

---

## Expression des Besoins

### Infrastructure
- Assurer la scalabilité et la résilience
- Conteneurisation des composants
- Sécurité robuste (authentification/autorisation)
- Intégration et déploiement continu automatisé
- Adaptation par pays avec infrastructures dédiées

### Gestion de Projet
- Mise en place de cérémonies agiles
- Rapports d'avancement tous les 3 jours
- Gestion complète du projet agile

---

## Livrables Attendus

### Partie Technique
1. Scripts et configurations pour le déploiement d'infrastructure
2. Fichiers Docker/Podman pour la conteneurisation
3. Mécanismes de sauvegarde et restauration
4. Pipelines CI/CD (build, test, analyse, déploiement)
5. Documentation des pipelines et images Docker
6. Rapports de tests automatisés et qualité de code
7. Documentation architecture système (UML)

### Partie Gestion de Projet
1. Rapports de sprint
2. Tableaux Kanban/Scrum
3. Présentation des cérémonies agiles

### Bonus
- Environnement Kubernetes (mono-nœud) avec documentation

---

## Méthodologie

**Phase 1** : Préparation (19 heures en équipe de 4-5 apprenants)
- Infrastructure supportant la charge
- Stratégie de conteneurisation
- Sécurisation de l'application
- Déploiement automatisé
- Gestion de projet Agile

**Phase 2** : Présentation orale collective (50 minutes)
- 20 min de soutenance
- 30 min d'entretien avec le jury

---

## Webographie

- [Docker Documentation](https://docs.docker.com/)
- [Kubernetes Documentation](https://kubernetes.io/docs/home/)
- [Jenkins Documentation](https://www.jenkins.io/doc/)
- [GitLab CI Documentation](https://docs.gitlab.com/ee/ci/)
- [GitHub Actions](https://docs.github.com/fr/actions)
- [SonarQube Documentation](https://docs.sonarqube.org/latest/)
- [Scrum Guide](https://scrumguides.org/)
- [Jira Documentation](https://www.atlassian.com/software/jira)
- [Trello](https://trello.com/en)
- [OWASP Top Ten](https://owasp.org/www-project-top-ten/)
- [GDPR Guidelines](https://gdpr.eu/)
- [Minikube Documentation](https://minikube.sigs.k8s.io/docs/start/)