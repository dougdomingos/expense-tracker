# Expense Tracker

**Expense Tracker** é uma aplicação Spring Boot desenvolvida para facilitar
o gerenciamento de finanças. O projeto foi desenvolvido como forma de
aprofundar meus conhecimentos em Spring Boot e praticar Design Patterns.

## User Stories

### US-01: Criação de usuários

Enquanto novo usuário, quero utilizar o sistema para me cadastrar como
usuário do sistema.

- Usuários são criados a partir de um nome e uma senha

- Ao se cadastrar no sistema, o usuário recebe automaticamente um token
  de Login

### US-02: Autenticação de usuários

Enquanto usuário cadastrado, quero ser capaz de fazer Login no sistema para
gerenciar minhas despesas e acessar minhas informações.

- Para fazer Login, o usuário deve informar seu nome e senha

- Apenas usuários autenticados no sistema podem visualizar, modificar e/ou
  excluir seus próprios cadastros

### US-03: Gerenciamento de Transações

Enquanto usuário autenticado, quero utilizar o sistema para gerenciar
minhas receitas e despesas mensais.

- Deve ser possível ler, editar e remover transações

- Transações são criadas a partir de um título, uma descrição (opcional),
  o valor da transação e sua data de criação

- Ao listar transações, também será informado o saldo atual do usuário

- O usuário poderá listar transações pelo tipo despesas ou receitas. A
  listagem deve incluir o valor total das transações daquele tipo.

### US-04: Transações fixas

Enquanto usuário autenticado, quero ser capaz de definir transações,
tanto despesas quanto receitas, que se repetem mensalmente.

- Transações fixas são automaticamente incluídas no saldo mensal

- Transações fixas podem ser canceladas. Uma vez cancelada, a transação
  não voltará a ser repetir a partir do próximo mês.

- Cancelar uma transação fixa não a remove do saldo do mês atual

### US-05: Categorias de Transações

Enquanto usuário autenticado, quero utilizar o sistema para classificar
minhas transações em categorias.

- Deve ser possível ler, editar e remover categorias

- Categorias são criadas com um nome e o tipo de transação que ela 
  representa (despesas ou receitas)

- O usuário pode adicionar uma transação já cadastrada a uma categoria.
  A transação deve ser do mesmo tipo da categoria a qual ela será 
  adicionada

- O usuário também pode remover uma transação de uma categoria

- Ao listar uma categoria específica, deve ser incluído o valor total
  das transações associadas a ela

### US-06: Alertas de Despesa

Enquanto usuário autenticado, quero ser notificado quando o valor total 
das minhas despesas alcançarem um determinado valor.

- O usuário pode criar alertas para o total de todas as despesas ou
  para uma categoria específica

- Quando um alerta for ativado, o usuário será notificado da causa
  do alerta

### US-07: Relatório Mensal

Enquanto usuário autenticado, quero ser capaz de gerar um relatório
com uma visão geral das transações realizadas durante o mês.

- O relatório deve incluir o saldo atual no mês e as categorias
  presentes no sistema

- Categorias devem ser ordenadas pelo tipo (receitas, depois despesas)
  e pelo valor (em ordem decrescentes)

- Categorias com valor total nulo não devem ser incluídas no
  relatório

- Transações não associadas a nenhuma categoria devem ser exibidas
  como uma categoria temporária (como "outros")

