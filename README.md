# Integração de serviço SMS


Para essa integração preferi utilizar o approach do camel. Apache camel é um framework de integrações que abstrai os patterns do EAI(http://www.enterpriseintegrationpatterns.com/), muito bacana
e bem simples de ser utilizado. Além de simples, o camel oferece alguns recursos extras, como metricas de execução e monitoramento. 



A escolha da utilização do camel ao invês do spring mvc, que aliás, seria um bom approach também, poderiamos ter algo iniciando assim >>


@Controller
@RequestMapping("/kfc/brands")
public class JSONController {

	@RequestMapping(value="{name}", method = RequestMethod.GET)
	public @ResponseBody Shop getShopInJSON(@PathVariable String name) {

		Shop shop = new Shop();
		shop.setName(name);
		shop.setStaffName(new String[]{"mkyong1", "mkyong2"});

		return shop;

	}
}

O apache camel usa dsl, deixando o codigo bem legível, para integração é uma ótima ferramenta. 


# 1º Mock da api gerada com swagger. 

A documentação disponibilizada no formato yaml, pode ser facilmente gerada o fonte para acesso ao serviço. 
Poderia fazer a integração da api diretamente usando as informações contidas no arquivo com o RestTemplate do spring, e utilizar o RestTemplateMock para fazer o mock. Ficaria simples e rapidamente teriamos a integração.
Mas foi disponibilizado o arquivo yaml, seria bacana gerar o stub a partir dela, diminuindo a quantidade de código a manter. Infelizmente, ainda não temos uma geração de stubs com resttemplate do spring, todavia, porém,
 o gerador usa jersey e jackson, para comunição no serviço. Mockar diretamente o Jersey dentro do código gerado é impossível, uma vez que a quantidade de objetos criados e privados é alta. Mas podemos utilizar o projeto do jersey e criar um webservice para os testes mock.

# 2º Acesso ao serviço

Dividi o projeto em dois serviços: 
	* Serviço de envio de SMS - Serviço disponibilizado para o envio do SMS através de Fila request-reply, caso a operadora esteja fora do ar, uma nova tentativa é feita depois.
	* Serviço de consulta de SMS - Verifica se o sms foi enviado corretamente para a operadora. 

# 3º Documentação da api sendo gerada com o swagger

Através da integração do camel com o swagger, é possível gerar a documentação no formato json. Basta acessar a url:
 http://127.0.0.1:8080/api-docs/camel-1


# 4º hawt.io - Ótimo gerenciador para o camel

Hawt.io tem os gerenciadores necessários para o camel. 
http://127.0.0.1:8080/hawtio
 
# 5º docker - build and deliver

//Ainda a ser testado corretamente, coloquei o plugin. 

# 6º Load test com SOAPUI

//Ainda a ser feito.