import java.io.File;
import java.io.IOException;
import java.util.List;

import org.bukkit.Bukkit;
import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.configuration.file.FileConfiguration;
import org.bukkit.configuration.file.YamlConfiguration;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.player.AsyncPlayerChatEvent;
import org.bukkit.event.player.PlayerJoinEvent;
import org.bukkit.plugin.java.JavaPlugin;

public class TitleCollectionPlugin extends JavaPlugin implements Listener, CommandExecutor
{
	@Override
	public void onEnable()
	{
		getCommand("tc").setExecutor(this);
		getServer().getPluginManager().registerEvents(this, this);

		createFiles();
	}

	@Override
	public void onDisable() {}

	public void createFiles()
	{
		if(!getDataFolder().exists())
			getDataFolder().mkdir();

		File titres = new File("plugins" + File.separator + "TitleCollectionPlugin" + File.separator + "titres.yml");
		if(!titres.exists())
		{
			try 
			{
				titres.createNewFile();
				FileConfiguration fc = YamlConfiguration.loadConfiguration(titres);
				fc.set("titles", new String[] {"Newbie", "Maître", "Dieu"});
				fc.save(titres);
			} 
			catch (IOException e) 
			{
				e.printStackTrace();
			}
		}
	}

	@EventHandler
	public void onPlayerJoin(PlayerJoinEvent event)
	{
		String player = event.getPlayer().getUniqueId().toString();
		File file = new File("plugins" + File.separator + "TitleCollectionPlugin" + File.separator + player +".yml");
		if(!file.exists())
		{
			try 
			{
				file.createNewFile();
				FileConfiguration fc = YamlConfiguration.loadConfiguration(file);
				fc.set("available-titles", new String[] {"aucun", "Newbie"});
				fc.set("current-title", "aucun");
				fc.set("last-known-as", event.getPlayer().getName());

				fc.save(file);
			} 
			catch (IOException e) 
			{
				e.printStackTrace();
			}
		}
		else
		{
			Player p = event.getPlayer();
			FileConfiguration fc = YamlConfiguration.loadConfiguration(file);
			fc.set("last-known-as", p.getName());
			try 
			{
				fc.save(file);
			} 
			catch (IOException e) 
			{
				e.printStackTrace();
			}
		}
	}

	@EventHandler
	public void onPlayerChatEvent(AsyncPlayerChatEvent event)
	{
		String player = event.getPlayer().getUniqueId().toString();
		File file = new File("plugins" + File.separator + "TitleCollectionPlugin" + File.separator + player +".yml");
		FileConfiguration fc = YamlConfiguration.loadConfiguration(file);

		String title = (String) fc.get("current-title");

		if(title.equals("aucun"))
			return;

		String[] format = event.getFormat().split(">");

		event.setFormat(format[0] + ", §6" + title + "§f>" + format[1]);	
	}

	@SuppressWarnings("unchecked")
	@Override
	public boolean onCommand(CommandSender sender, Command cmd, String label, String[] args)
	{
		if(sender instanceof Player)
		{
			Player player = (Player) sender;

			if(!cmd.getName().equals("tc"))
				return false;

			if(args.length == 0)
			{
				player.sendMessage("§cMauvaise utilisation de la commande.");
				return false;
			}

			if(args.length == 1)
			{
				switch(args[0])
				{
				case "help":
					player.sendMessage("§6--- Liste des commandes de TitleCollection ---");

					player.sendMessage("§b/tc help §f- affiche la liste des différentes commandes"); // DONE
					player.sendMessage("§b/tc list §f- affiche la liste de vos titres disponibles"); // DONE
					player.sendMessage("§b/tc list pseudo §f- affiche la liste des titres du joueur précisé (admin)"); // DONE
					player.sendMessage("§b/tc listadmin §f- affiche la liste de tous les titres (admin)"); // DONE
					player.sendMessage("§b/tc add pseudo titre §f- ajoute le titre précisé au joueur indiqué (admin)"); // DONE
					player.sendMessage("§b/tc addadmin titre §f- ajoute un nouveau titre à la liste des titres (admin)"); // DONE
					player.sendMessage("§b/tc remove pseudo titre §f- retire un le titre au joueur spécifié (admin)"); // DONE
					player.sendMessage("§b/tc removeadmin titre §f- retire le titre spécifié de la liste de tous les titres (admin)"); // DONE
					break;
				case "list":
					File playerTitle = new File("plugins" + File.separator + "TitleCollectionPlugin" + File.separator + player.getUniqueId().toString() + ".yml");
					FileConfiguration playerTitleYml = YamlConfiguration.loadConfiguration(playerTitle);
					List<String> playerTitleList = (List<String>) playerTitleYml.get("available-titles");

					player.sendMessage("§6--- Liste de vos titres ---");
					for(String s : playerTitleList)
					{
						player.sendMessage("§b - " + s);
					}

					break;
				case "listadmin":
					if(!player.isOp())
					{
						player.sendMessage("§cVous devez être Op pour utiliser cette commande.");
						return true;
					}

					File titres = new File("plugins" + File.separator + "TitleCollectionPlugin" + File.separator + "titres.yml");
					FileConfiguration fc = YamlConfiguration.loadConfiguration(titres);
					List<String> liste = (List<String>) fc.getList("titles");

					player.sendMessage("§6--- Liste de tous les titres ---");
					for(String s : liste)
					{
						player.sendMessage("§b - " + s);
					}

					break;
				default:
					player.sendMessage("§cMauvaise utilisation de la commande.");
					return false;
				}
			}

			if(args.length >= 2)
			{
				switch(args[0])
				{
				case "add":
					if(!player.isOp())
					{
						player.sendMessage("§cVous devez être Op pour utiliser cette commande.");
						return true;
					}

					Player target = Bukkit.getPlayer(args[1]); 
					if(target == null)
					{
						player.sendMessage("§cLe joueur spécifié n'existe pas.");
						return true;
					}

					// On regarde s'il y a un titre ensuite
					if(args.length <= 2)
					{
						player.sendMessage("§cAucun titre spécifié.");
						return true;
					}

					// On construit notre titre 
					String titre = "";
					for(int i = 2 ; i < args.length ; i++)
					{
						titre += args[i] + " ";
					}
					titre = titre.trim();

					// On regarde si le titre existe dans titres.yml
					File titres = new File("plugins" + File.separator + "TitleCollectionPlugin" + File.separator + "titres.yml");
					FileConfiguration fc = YamlConfiguration.loadConfiguration(titres);
					List<String> liste = (List<String>) fc.getList("titles");

					boolean found = false;
					for(String s : liste)
					{
						if(s.trim().equals(titre))
						{
							found = true;
							break;
						}
					}

					if(!found)
					{
						player.sendMessage("§cCe titre n'existe pas.");
						return true;
					}

					// On regarde si le joueur ne le possède pas déjà
					File playerTitle = new File("plugins" + File.separator + "TitleCollectionPlugin" + File.separator + target.getUniqueId().toString() + ".yml");
					FileConfiguration playerTitleYml = YamlConfiguration.loadConfiguration(playerTitle);
					List<String> playerTitleList = (List<String>) playerTitleYml.get("available-titles");

					found = false;
					for(String s : playerTitleList)
					{
						if(s.equals(titre))
						{
							found = true;
							break;
						}
					}

					if(found)
					{
						player.sendMessage("§cLe joueur possède déjà ce titre.");
						return true;
					}

					// On lui ajoute son titre
					playerTitleList.add(titre);
					playerTitleYml.set("available-titles", playerTitleList.toArray());
					try 
					{
						playerTitleYml.save(playerTitle);
						player.sendMessage("§bLe titre a été ajouté avec succès.");
						target.sendMessage("§bVous avez accès au nouveau titre : §6" + titre + "§b !");
					} 
					catch (IOException e) 	
					{
						e.printStackTrace();
					}
					return true;
				case "addadmin":
					if(!player.isOp())
					{
						player.sendMessage("§cVous devez être Op pour utiliser cette commande.");
						return true;
					}

					// On construit le titre
					String nouveauTitre = "";
					for(int i = 1 ; i < args.length ; i++)
					{
						nouveauTitre += args[i] + " ";
					}
					nouveauTitre = nouveauTitre.trim();

					// On regarde si le titre n'est pas déjà présent dans titres.yml
					File fichierTitres = new File("plugins" + File.separator + "TitleCollectionPlugin" + File.separator + "titres.yml");
					FileConfiguration fcTitres = YamlConfiguration.loadConfiguration(fichierTitres);
					List<String> listeTitres = (List<String>) fcTitres.getList("titles");

					boolean present = false;
					for(String s : listeTitres)
					{
						if(s.trim().equals(nouveauTitre))
						{
							present = true;
							break;
						}
					}

					if(present)
					{
						player.sendMessage("§cLe titre existe déjà.");
						return true;
					}

					// On ajoute le nouveau titre
					listeTitres.add(nouveauTitre);

					fcTitres.set("titles", listeTitres.toArray());
					try 
					{
						fcTitres.save(fichierTitres);
						player.sendMessage("§bLe titre a été ajouté avec succès.");
					} 
					catch (IOException e) 
					{
						e.printStackTrace();
					}

					return true;
				case "use":
					// On construit le titre
					String titleToUse = "";
					for(int i = 1 ; i < args.length ; i++)
					{
						titleToUse += args[i] + " ";
					}
					titleToUse = titleToUse.trim();


					{
						// On vérifie que le joueur possède bien le titre 
						File titleFile = new File("plugins" + File.separator + "TitleCollectionPlugin" + File.separator + player.getUniqueId().toString() + ".yml");
						FileConfiguration titleFileYml = YamlConfiguration.loadConfiguration(titleFile);
						List<String> titleFileList = (List<String>) titleFileYml.get("available-titles");

						boolean possessed = false;
						for(String s : titleFileList)
						{
							if(s.trim().equals(titleToUse))
							{
								possessed = true;
								break;
							}
						}

						if(!possessed)
						{
							player.sendMessage("§cVous ne pouvez pas utiliser un titre que vous ne possédez pas.");
							return true;
						}

						// On modifie son titre courant
						titleFileYml.set("current-title", titleToUse);
						try 
						{
							titleFileYml.save(titleFile);
							player.sendMessage("§bVotre titre courant a bien été changé.");
						} 
						catch (IOException e) 
						{
							e.printStackTrace();
						}
					}


					break;
				case "remove":
				{
					// on vérifie si le joueur est op
					if(!player.isOp())
					{
						player.sendMessage("§cVous devez être Op pour utiliser cette commande.");
						return true;
					}

					// on vérifie si la cible est un joueur valide
					Player targetRemove = Bukkit.getPlayer(args[1]);
					if(targetRemove == null)
					{
						player.sendMessage("§cLe joueur spécifié est invalide.");
						return true;
					}

					if(args.length <= 2)
					{
						player.sendMessage("§cAucun titre spécifié.");
						return true;
					}

					// on recupère le titre
					String titreRemove = "";
					for(int i = 2 ; i < args.length ; i++)
					{
						titreRemove += args[i] + " ";
					}
					titreRemove = titreRemove.trim();

					// on vérifie que le joueur possede bien le titre
					File titleFile = new File("plugins" + File.separator + "TitleCollectionPlugin" + File.separator + targetRemove.getUniqueId().toString() + ".yml");
					FileConfiguration titleFileYml = YamlConfiguration.loadConfiguration(titleFile);
					List<String> titleFileList = (List<String>) titleFileYml.get("available-titles");

					boolean possessed = false;
					for(String s : titleFileList)
					{
						if(s.trim().equals(titreRemove))
						{
							possessed = true;
							break;
						}
					}

					if(!possessed)
					{
						player.sendMessage("§cLe joueur ne possède pas le titre spécifié.");
						return true;
					}

					// on retire le titre au joueur
					for(String s : titleFileList)
					{
						if(s.trim().equals(titreRemove))
						{
							titleFileList.remove(s);
							break;
						}
					}

					titleFileYml.set("available-titles", titleFileList);
					try 
					{
						titleFileYml.save(titleFile);
						player.sendMessage("§bLe titre à bien été retiré.");
						targetRemove.sendMessage("§bVous avez perdu le titre : §6" + titreRemove + "§b.");
					} 
					catch (IOException e) 
					{
						e.printStackTrace();
					}

					break;
				}
				case "list":
				{
					// on vérifie si le joueur est op
					if(!player.isOp())
					{
						player.sendMessage("§cVous devez être Op pour utiliser cette commande.");
						return true;
					}

					// on vérifie que le joueur saisie est valide
					Player targetList = Bukkit.getPlayer(args[1]);
					if(targetList == null)
					{
						player.sendMessage("§cLe joueur spécifié est invalide.");
						return true;
					}

					// on recupere ses titres et on les affiches
					File titleFile = new File("plugins" + File.separator + "TitleCollectionPlugin" + File.separator + targetList.getUniqueId().toString() + ".yml");
					FileConfiguration titleFileYml = YamlConfiguration.loadConfiguration(titleFile);
					List<String> titleFileList = (List<String>) titleFileYml.get("available-titles");

					player.sendMessage("§6--- Liste des titres de " + targetList.getName() + " ---");
					for(String s : titleFileList)
					{
						player.sendMessage("§b- " + s);
					}

					break;
				}
				case "removeadmin": 
				{
					// on vérifie que le joueur soit OP
					if(!player.isOp())
					{
						player.sendMessage("§cVous devez être Op pour utiliser cette commande.");
						return true;
					}

					// on récupère le titre
					String titreRemove = "";
					for(int i = 1 ; i < args.length ; i++)
					{
						titreRemove += args[i] + " ";
					}
					titreRemove = titreRemove.trim();

					// on vérifie que le titre existe
					File titleFile = new File("plugins" + File.separator + "TitleCollectionPlugin" + File.separator + "titres.yml");
					FileConfiguration titleFileYml = YamlConfiguration.loadConfiguration(titleFile);
					List<String> titleFileList = (List<String>) titleFileYml.get("titles");

					boolean posseder = false;
					for(String s : titleFileList)
					{
						if(s.trim().equals(titreRemove))
						{
							posseder = true;
							titleFileList.remove(s);
							break;
						}
					}

					if(!posseder)
					{
						player.sendMessage("§cLe titre spécifié n'existe pas.");
						return true;
					}

					titleFileYml.set("titles", titleFileList);
					try 
					{	
						titleFileYml.save(titleFile);
						player.sendMessage("§bLe titre à bien été retiré de la liste générale.");
					} 
					catch (IOException e) 
					{
						e.printStackTrace();
					}

					break;
				}
				default:
					player.sendMessage("§cMauvaise utilisation de la commande.");
					return false;
				}
			}
		}	
		else
		{
			// Cas où sender est pas un Joueur mais un plugin
			if(args.length >= 2)
			{
				if(args[0].equals("add"))
				{
					Player target = Bukkit.getPlayer(args[1]); 
					if(target == null)
						return true;

					// On regarde s'il y a un titre ensuite
					if(args.length <= 2)
						return true;

					// On construit notre titre 
					String titre = "";
					for(int i = 2 ; i < args.length ; i++)
					{
						titre += args[i] + " ";
					}
					titre = titre.trim();

					// On regarde si le titre existe dans titres.yml
					File titres = new File("plugins" + File.separator + "TitleCollectionPlugin" + File.separator + "titres.yml");
					FileConfiguration fc = YamlConfiguration.loadConfiguration(titres);
					List<String> liste = (List<String>) fc.getList("titles");

					boolean found = false;
					for(String s : liste)
					{
						if(s.trim().equals(titre))
						{
							found = true;
							break;
						}
					}

					if(!found)
						return true;

					// On regarde si le joueur ne le possède pas déjà
					File playerTitle = new File("plugins" + File.separator + "TitleCollectionPlugin" + File.separator + target.getUniqueId().toString() + ".yml");
					FileConfiguration playerTitleYml = YamlConfiguration.loadConfiguration(playerTitle);
					List<String> playerTitleList = (List<String>) playerTitleYml.get("available-titles");

					found = false;
					for(String s : playerTitleList)
					{
						if(s.equals(titre))
						{
							found = true;
							break;
						}
					}

					if(found)
						return true;

					// On lui ajoute son titre
					playerTitleList.add(titre);
					playerTitleYml.set("available-titles", playerTitleList.toArray());
					try 
					{
						playerTitleYml.save(playerTitle);
						target.sendMessage("§bVous avez accès au nouveau titre : §6" + titre + "§b !");
					} 
					catch (IOException e) 	
					{
						e.printStackTrace();
					}
					return true;
				}
			}
		}
		return true;
	}
}