import { useTranslation } from 'react-i18next';
import { Languages } from 'lucide-react';

export const LanguageSwitcher: React.FC = () => {
  const { i18n } = useTranslation();

  const languages = [
    { code: 'ja', label: '日本語' },
    { code: 'en', label: 'English' },
  ];

  const changeLanguage = (lng: string) => {
    i18n.changeLanguage(lng);
    localStorage.setItem('language', lng);
  };

  return (
    <div className="relative group inline-block">
      <button className="flex items-center gap-2 px-3 py-2 text-sm text-gray-700 hover:bg-gray-100 rounded-md transition-colors">
        <Languages className="w-4 h-4" />
        <span className="hidden md:inline">
          {languages.find((l) => l.code === i18n.language)?.label || '日本語'}
        </span>
      </button>
      
      <div className="absolute left-0 bottom-full mb-2 w-40 bg-white rounded-md shadow-lg border border-gray-200 opacity-0 invisible group-hover:opacity-100 group-hover:visible transition-all z-50">
        {languages.map((lang) => (
          <button
            key={lang.code}
            onClick={() => changeLanguage(lang.code)}
            className={`block w-full text-left px-4 py-2 text-sm hover:bg-gray-100 first:rounded-t-md last:rounded-b-md ${
              i18n.language === lang.code ? 'bg-gray-50 font-medium text-primary' : 'text-gray-700'
            }`}
          >
            {lang.label}
          </button>
        ))}
      </div>
    </div>
  );
};
