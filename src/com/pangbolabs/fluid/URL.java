package com.pangbolabs.fluid;

import java.net.MalformedURLException;

public class URL
{
	private String scheme = "";
	private String userName = "";
	private String password = "";
	private String host = "";
	private int port = 0;
	private String path = "";
	private String queryString = "";
	private String fragment = "";
	
	public URL()
	{
	}
	
	public URL( String urlString ) throws MalformedURLException
	{
		this.parse( urlString );
	}
	
	private enum ParsingState
	{
		Begin,
		ReadingSchemeTemp,		// possible components: scheme, path, query-string, fragment
		ReadingAuthorityTemp1,	// possible components: user-name, host
		ReadingAuthorityTemp2,	// possible components: password, port
		ReadingHost,
		ReadingPort,
		ReadingPath,
		ReadingQueryString,
		ReadingFragment,
	}
	
	// URL = [scheme "://" [user-name [":" password] "@"] host [":" port]] [path] ["?" query-string] ["#" fragment]
	private void parse( String urlString ) throws MalformedURLException
	{
		String authorityTemp = null;
		
		StringBuilder sb = new StringBuilder();
		ParsingState state = ParsingState.Begin;
		int i = 0;
		while (i < urlString.length())
		{
			char ch = urlString.charAt( i );
			switch (state)
			{
			case Begin:
				if ((ch == '/') || (ch == '.'))
				{
					sb.append( (char)ch );
					i ++;
					state = ParsingState.ReadingPath;
				}
				else if (ch == '?')
				{
					sb.append( (char)ch );
					i ++;
					state = ParsingState.ReadingQueryString;
				}
				else if (ch == '#')
				{
					sb.append( (char)ch );
					i ++;
					state = ParsingState.ReadingFragment;
				}
				else
				{
					sb.append( (char)ch );
					i ++;
					state = ParsingState.ReadingSchemeTemp;
				}
				break;

			case ReadingSchemeTemp: // possible components: scheme, path, query-string, fragment
				if (ch == ':')
				{
					if (i > urlString.length() - 3)
						throw new MalformedURLException(); // no "://"
					if ((urlString.charAt( i + 1 ) != '/') || (urlString.charAt( i + 2 ) != '/'))
						throw new MalformedURLException(); // no "://"
					this.scheme = getAndClear( sb );
					i += 3;
					state = ParsingState.ReadingAuthorityTemp1;
				}
				else if (ch == '/') // it's a relative URL, e.g. abc/def/xyz.html
				{
					sb.append( (char)ch );
					i ++;
					state = ParsingState.ReadingPath;
				}
				else if (ch == '.') // it's a file name, e.g. xyz.html
				{
					sb.append( (char)ch );
					i ++;
					state = ParsingState.ReadingPath;
				}
				else if (ch == '?') // it's a file name and a query string, e.g. file?a=1&b=2
				{
					sb.append( (char)ch );
					i ++;
					state = ParsingState.ReadingQueryString;
				}
				else if (ch == '#') // it's a file name and a fragment, e.g. file#abc
				{
					sb.append( (char)ch );
					i ++;
					state = ParsingState.ReadingFragment;
				}
				else if (ch == '@')
				{
					throw new MalformedURLException();
				}
				else
				{
					sb.append( (char)ch );
					i ++;
				}
				break;

			case ReadingAuthorityTemp1: // possible components: user-name, host
				if (ch == ':')
				{
					authorityTemp = getAndClear( sb );
					i ++;
					state = ParsingState.ReadingAuthorityTemp2;
				}
				else if (ch == '@')
				{
					this.userName = getAndClear( sb );
					i ++;
					state = ParsingState.ReadingHost;
				}
				else if (ch == '/')
				{
					this.host = getAndClear( sb );
					i ++;
					state = ParsingState.ReadingPath;
				}
				else if ((ch == '?') || (ch == '#'))
				{
					throw new MalformedURLException();
				}
				else
				{
					sb.append( (char)ch );
					i ++;
				}
				break;

			case ReadingAuthorityTemp2: // possible components: password, port
				if (ch == ':')
				{
					throw new MalformedURLException();
				}
				else if (ch == '@')
				{
					this.userName = authorityTemp;
					this.password = getAndClear( sb );
					i ++;
					state = ParsingState.ReadingHost;
				}
				else if (ch == '/')
				{
					this.host = authorityTemp;
					this.port = parseIntString( getAndClear( sb ) );
					i ++;
					state = ParsingState.ReadingPath;
				}
				else if ((ch == '?') || (ch == '#'))
				{
					throw new MalformedURLException();
				}
				else
				{
					sb.append( (char)ch );
					i ++;
				}
				break;

			case ReadingHost:
				if (ch == ':')
				{
					this.host = getAndClear( sb );
					i ++;
					state = ParsingState.ReadingPort;
				}
				else if (ch == '/')
				{
					this.host = getAndClear( sb );
					i ++;
					state = ParsingState.ReadingPath;
				}
				else if ((ch == '@') || (ch == '?') || (ch == '#'))
				{
					throw new MalformedURLException();
				}
				else
				{
					sb.append( (char)ch );
					i ++;
				}
				break;

			case ReadingPort:
				if (ch == '/')
				{
					this.port = parseIntString( getAndClear( sb ) );
					i ++;
					state = ParsingState.ReadingPath;
				}
				else if (!Character.isDigit( ch ))
				{
					throw new MalformedURLException();
				}
				else
				{
					sb.append( (char)ch );
					i ++;
				}
				break;

			case ReadingPath:
				if (ch == '?')
				{
					this.path = getAndClear( sb );
					i ++;
					state = ParsingState.ReadingQueryString;
				}
				else if (ch == '#')
				{
					this.path = getAndClear( sb );
					i ++;
					state = ParsingState.ReadingFragment;
				}
				else if ((ch == '@') || (ch == ':'))
				{
					throw new MalformedURLException();
				}
				else
				{
					sb.append( (char)ch );
					i ++;
				}
				break;

			case ReadingQueryString:
				if (ch == '#')
				{
					this.queryString = getAndClear( sb );
					i ++;
					state = ParsingState.ReadingFragment;
				}
				else if ((ch == '@') || (ch == ':') || (ch == '?'))
				{
					throw new MalformedURLException();
				}
				else
				{
					sb.append( (char)ch );
					i ++;
				}
				break;

			case ReadingFragment:
				if ((ch == '@') || (ch == ':') || (ch == '?') || (ch == '#'))
				{
					throw new MalformedURLException();
				}
				else
				{
					sb.append( (char)ch );
					i ++;
				}
				break;

			default:
				sb.append( (char)ch );
				i ++;
			}
		}
		
		switch (state)
		{
		case ReadingAuthorityTemp1: // possible components: user-name, host
			if (sb.length() == 0) throw new MalformedURLException();
			this.host = getAndClear( sb );
			break;
			
		case ReadingAuthorityTemp2: // possible components: password, port
			if (sb.length() == 0) throw new MalformedURLException();
			this.host = authorityTemp;
			this.port = parseIntString( getAndClear( sb ) );
			break;
			
		case ReadingHost:
			if (sb.length() == 0) throw new MalformedURLException();
			this.host = getAndClear( sb );
			break;
			
		case ReadingPort:
			this.port = parseIntString( getAndClear( sb ) );
			break;
			
		case ReadingPath:
			this.path = getAndClear( sb );
			break;
			
		case ReadingQueryString:
			this.queryString = getAndClear( sb );
			break;
			
		case ReadingFragment:
			this.fragment = getAndClear( sb );
			break;
			
		default:
			throw new MalformedURLException();
		}
	}
	
	private String getAndClear( StringBuilder sb )
	{
		String s = sb.toString();
		clearStringBuilder( sb );
		return s;
	}
	
	private void clearStringBuilder( StringBuilder sb )
	{
		sb.delete( 0, sb.length() );
	}
	
	private int parseIntString( String intString )
	{
		if ((intString == null) || (intString.length() == 0))
			return 0;
		return Integer.parseInt( intString );
	}
	
	public String getScheme()
	{
		return scheme;
	}

	public void setScheme( String scheme )
	{
		this.scheme = scheme;
	}

	public String getUserName()
	{
		return userName;
	}

	public void setUserName( String userName )
	{
		this.userName = userName;
	}

	public String getPassword()
	{
		return password;
	}

	public void setPassword( String password )
	{
		this.password = password;
	}

	public String getHost()
	{
		return host;
	}

	public void setHost( String host )
	{
		this.host = host;
	}

	public int getPort()
	{
		return port;
	}

	public void setPort( int port )
	{
		this.port = port;
	}

	public String getPath()
	{
		return path;
	}

	public void setPath( String path )
	{
		this.path = path;
	}

	public String getQueryString()
	{
		return queryString;
	}

	public void setQueryString( String queryString )
	{
		this.queryString = queryString;
	}

	public String getFragment()
	{
		return fragment;
	}

	public void setFragment( String fragment )
	{
		this.fragment = fragment;
	}

	@Override
	public String toString()
	{
		StringBuilder sb = new StringBuilder();
		sb.append( this.getClass().getSimpleName() + " {\n" );
		sb.append( String.format( "  scheme: '%s',\n", scheme ) );
		sb.append( String.format( "  userName: '%s',\n", userName ) );
		sb.append( String.format( "  password: '%s',\n", password ) );
		sb.append( String.format( "  host: '%s',\n", host ) );
		sb.append( String.format( "  port: %d,\n", port ) );
		sb.append( String.format( "  path: '%s',\n", path ) );
		sb.append( String.format( "  queryString: '%s',\n", queryString ) );
		sb.append( String.format( "  fragment: '%s',\n", fragment ) );
		sb.append( "}\n" );
		return sb.toString();
	}
	
	public static void main( String[] args )
	{
		String[] urlStrs = {
			"http://user:pwd@www.microsoft.com:8081/path1/path2/path3.html?a=52&b=61&c=78#frag",
			"http://user:@www.microsoft.com:8081/path1/path2/path3.html?a=52&b=61&c=78#frag",
			"http://user@www.microsoft.com:8081/path1/path2/path3.html?a=52&b=61&c=78#frag",
			"http://user@www.microsoft.com:/path1/path2/path3.html?a=52&b=61&c=78#frag",
			"http://user@www.microsoft.com/path1/path2/path3.html?a=52&b=61&c=78#frag",
			"http://user@www.microsoft.com/path1/path2/path3.html?#frag",
			"http://user@www.microsoft.com/path1/path2/path3.html#frag",
			"http://user@www.microsoft.com/path1/path2/path3.html#",
			"http://user@www.microsoft.com/path1/path2/path3.html",
			"http://www.microsoft.com/path1/path2/path3.html",
			"/path1/path2/path3.html",
			"path1/path2/path3.html",
			"../path1/path2/path3.html",
			"./path1/path2/path3.html",
			"path3.html",
			"?a=52&b=61&c=78#frag",
			"?#frag",
			"#frag",
			"#",
		};
		
		for (int i = 0; i < urlStrs.length; i ++)
		{
			System.out.println( "Source: " + urlStrs[i] );
			try
			{
				URL url = new URL( urlStrs[i] );
				System.out.println( "Result: " + url );
			}
			catch (Exception e)
			{
				e.printStackTrace();
			}
		}
	}
}
