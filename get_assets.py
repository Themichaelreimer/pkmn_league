from lxml import html
import requests

def get_html():
	return requests.get("https://pokemondb.net/pokedex/all").content
	
def parse_page(webpage:str):
    tree = html.fromstring(webpage)
    pokemon_names = tree.xpath('//a[@class="ent-name"]/text()')
    pokemon_numbers = tree.xpath('//span[@class="infocard-cell-data"]/text()')
    pokemon_stats = tree.xpath('//tr/td/text()')
    pokemon_types = tree.xpath('//td[@class="cell-icon"]/text()')
    
    import pdb
    pdb.set_trace()
    
    return pokemon_rows
    
page = get_html()
parse_page(page)